#include "netbuffer.h"
#include "mailuser.h"
#include "server.h"
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/utsname.h>
#include <ctype.h>
#include <string.h>

#define MAX_LINE_LENGTH 1024

static void handle_client(int fd);

// State machine model for POP3
struct pop3_machine {
    bool is_locked;
    bool expect_pass;
    // State 1: AUTHORIZATION
    // State 2: TRANSACTION
    // State 3: UPDATE
    int state;
    char *current_username;
    user_list_t user_list;
};
// Initialize the state machine
struct pop3_machine* pop3_constructor() {
  struct pop3_machine* p = malloc(sizeof(struct pop3_machine));
  p->is_locked = false;
  p->expect_pass = false;
  p->state = 1;
  p->current_username = NULL;
  p->user_list = create_user_list();
  return p;
}

int main(int argc, char *argv[]) {

  if (argc != 2) {
    fprintf(stderr, "Invalid arguments. Expected: %s <port>\n", argv[0]);
    return 1;
  }

  run_server(argv[1], handle_client);

  return 0;
}

// Helper to check if string specifies a number
bool str_is_num(char* str) {
  int l = strlen(str);
  for (int i = 0; i < l - 1; i++) {
    if (!isdigit(str[i])) {
      return false;
    }
  }
  return true;
}


void handle_client(int fd) {
  // TODO To be implemented
  // Create client buffer
  net_buffer_t buff = nb_create(fd, MAX_LINE_LENGTH);
  send_string(fd, "+OK POP3 server ready\n");
  struct pop3_machine *popd = pop3_constructor();
  // Start listening on commands
  while(true) {
    // Read what the input command is
    char usrInput[MAX_LINE_LENGTH + 1];
    int in_len = nb_read_line(buff, usrInput);
    if (in_len < 4) {
      send_string(fd, "-ERR command not supported\n");
    }
    while (in_len >= 4) {
      // Could be a valid command
      char *command = malloc((sizeof (char)) * 5);
      strncpy(command, usrInput, 4);
      // Argument
      char *arg = malloc(MAX_LINE_LENGTH);
      if (in_len > 5) {
        memcpy(arg, &usrInput[5], ((int) strlen(usrInput)) - 6);
      } else {
        arg = NULL;
      }
      // USER
      if (strcasecmp(command, "user") == 0) {
        if (popd->state != 1) {
          send_string(fd, "-ERR USER command can only be issued at AUTHORIZATION state\n");
          break;
        }
        // Check if arg exists
        if (arg == NULL) {
          send_string(fd, "-ERR please enter a valid username\n");
          break;
        }
        // Begin authentication
        if (is_valid_user(arg, NULL) == 0) {
          send_string(fd, "-ERR no such mailbox\n");
          break;
        } else {
          send_string(fd, "+OK user exists, please enter password\n");
          // Update current user
          popd->current_username = arg;
          // Expect password for next command
          popd->expect_pass = true;
          break;
        }
        break;
      }

      // PASS
      if (strcasecmp(command, "pass") == 0) {
        if (popd->state != 1) {
          send_string(fd, "-ERR PASS command can only be issued at AUTHORIZATION State\n");
          break;
        }
        if (!popd->expect_pass) {
          send_string(fd, "-ERR please enter a user name first by using USER command\n");
          break;
        }
        // Check if theres an argument
        if (arg == NULL) {
          send_string(fd, "-ERR please enter a password\n");
          break;
        }
        // Check if maildrop is locked already
        if (popd->is_locked) {
          send_string(fd, "-ERR mailbox is already locked\n");
          popd->expect_pass = false;
          popd->current_username = NULL;
          break;
        }
        // Validate password
        if (is_valid_user(popd->current_username, arg) == 0) {
          send_string(fd, "-ERR invalid password, please begin authorization again by entering username\n");
          popd->expect_pass = false;
          popd->current_username = NULL;
          break;
        } else {
          send_string(fd, "+OK Mailbox locked and ready\n");
          popd->is_locked = true;
          popd->expect_pass = false;
          popd->state = 2;
          // Add user to list
          add_user_to_list(&popd->user_list, popd->current_username);
          break;
        }
        break;
      }

      // QUIT
      if (strcasecmp(command, "quit") == 0) {
        if (popd->state == 1) {
          send_string(fd, "+OK bye\n");
          free(popd);
          return;
        }
        if (popd->state == 2) {
          popd->state = 3;
          send_string(fd, "+OK deleting messages ... %d messages left\n", (int)get_mail_count(load_user_mail(popd->current_username)));
          destroy_mail_list(load_user_mail(popd->current_username));
          popd->is_locked = false;
          free(popd);
          return;
        }
        break;
      }

      // STAT
      if (strcasecmp(command, "stat") == 0) {
        if (popd->state != 2) {
          send_string(fd, "-ERR STAT command can only be issued in TRANSACTION state\n");
          break;
        } else {
          mail_list_t mails = load_user_mail(popd->current_username);
          unsigned int mailCount = get_mail_count(mails);
          size_t mailSize = get_mail_list_size(mails);
          send_string(fd, "+OK %d %d\n", (int) mailCount, (int) mailSize);
          break;
        }
        break;
      }

      // LIST
      if (strcasecmp(command, "list") == 0) {
        if (popd->state != 2) {
          send_string(fd, "-ERR LIST command can only be issued in TRANSACTION state\n");
          break;
        }

        // Check if there is an argument or not
        if (arg == NULL) {
          // Go through whole list mails and print
          mail_list_t mails = load_user_mail(popd->current_username);
          int mailCount = (int) get_mail_list_size(mails);
          if (mailCount == 0) {
            send_string(fd, "+OK 0 messages\n");
            send_string(fd, ".\r\n");
            break;
          }
          send_string(fd, "+OK %d messages in mailbox\n", mailCount);
          for (int pos = 0; pos < mailCount; pos++) {
            mail_item_t mail = get_mail_item(mails, pos);
            if (mail == NULL) {
              // mail is deleted, do nothing
            } else {
              send_string(fd, "+OK %d %d\n", pos, (int) get_mail_item_size(mail));
            }
          }
          break;
        } else {
          // Check if arg is a valid message number
          if (!str_is_num(arg)) {
            send_string(fd, "-ERR not a valid message number\n");
            break;
          } else {
            int pos = atoi(arg) - 1;
            mail_list_t mails = load_user_mail(popd->current_username);
            int mailCount = get_mail_count(mails);
            if (pos > mailCount) {
              // Invalid number
              send_string(fd, "-ERR theres only %d messages\n", mailCount);
              break;
            }
            mail_item_t mail = get_mail_item(mails, pos);
            if (mail == NULL) {
              // Cant list deleted mail
              send_string(fd, "-ERR mail is deleted\n");
              break;
            } else {
              send_string(fd, "+OK %d %d", pos, (int) get_mail_item_size(mail));
              break;
            }
            break;
          }
          break;
        }
        break;
      }

      // RETR
      if (strcasecmp(command, "retr") == 0) {
        if (popd->state != 2) {
          send_string(fd, "-ERR RETR command can only be issued in TRANSACTION state\n");
          break;
        }

        if (arg == NULL || strcmp(arg, "") == 0 || !str_is_num(arg)) {
          send_string(fd, "-ERR not a valid message number\n");
          break;
        } else {
          unsigned int pos = atoi(arg) - 1;
          mail_list_t mails = load_user_mail(popd->current_username);
          mail_item_t mail = get_mail_item(mails, pos);
          if (mail == NULL) {
            // Message is deleted
            send_string(fd, "-ERR message is deleted\n");
            break;
          } else {
            // Message is not deleted
            int size = (int) get_mail_item_size(mail);
            const char* fileName = get_mail_item_filename(mail);
            send_string(fd, "+OK %d octets\n", size);
            // Open mail file and print contents
            FILE* file = fopen(fileName, "r");
            if (file) {
              char c;
              int i = 0;
              while ((c = fgetc(file))) {
                if (i == 1023) {
                  send_string(fd, "\n");
                  i = 0;
                }
                send_string(fd, "%c", c);
                i++;
              }
              send_string(fd, ".\n");
              break;
            } else {
              send_string(fd, "-ERR message file could not be opened\n");
              break;
            }
            break;
          }
          break;
        }
        break;
      }

      // DELE
      if (strcasecmp(command, "dele") == 0) {
        if (popd->state != 2) {
          send_string(fd, "-ERR DELE command can only be issued in TRANSACTION state\n");
          break;
        }
        // Check if arg exists
        if (arg == NULL) {
          send_string(fd, "-ERR please specify a valid message number\n");
          break;
        }
        if (!str_is_num(arg)) {
          send_string(fd, "-ERR not a valid message number\n");
          break;
        } else {
          unsigned int pos = atoi(arg) - 1;
          mail_list_t mails = load_user_mail(popd->current_username);
          mail_item_t mail = get_mail_item(mails, pos);
          if (mail == NULL) {
            send_string(fd, "-ERR message already deleted\n");
            break;
          } else {
            mark_mail_item_deleted(mail);
            send_string(fd, "+OK message deleted\n");
            break;
          }
          break;
        }
        break;
      }

      // RSET
      if (strcasecmp(command, "rset") == 0) {
        if (popd->state != 2) {
          send_string(fd, "-ERR RSET command can only be issued in TRANSACTION state\n");
          break;
        } else {
          mail_list_t mails = load_user_mail(popd->current_username);
          int resetCount = reset_mail_list_deleted_flag(mails);
          mails = load_user_mail(popd->current_username);
          int mailCount = get_mail_count(mails);
          send_string(fd, "+OK %d mails recovered, %d total in mailbox\n", resetCount, mailCount);
          break;
        }
        break;
      }

      // NOOP
      if (strcasecmp(command, "noop") == 0) {
        if (popd->state != 2) {
          send_string(fd, "-ERR NOOP command can only be issued in TRANSACTION state\n");
          break;
        } else {
          send_string(fd, "+OK\n");
          break;
        }
        break;
      } else {
        send_string(fd, "-ERR command not supported\n");
        break;
      }
    }
  }
}