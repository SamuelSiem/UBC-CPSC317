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
#define STREQUAL(a,b) (strcmp(a, b) == 0)

static void handle_client(int fd);

int main(int argc, char *argv[]) {
  
  if (argc != 2) {
    fprintf(stderr, "Invalid arguments. Expected: %s <port>\n", argv[0]);
    return 1;
  }
  
  run_server(argv[1], handle_client);
  
  return 0;
}

void handle_client(int fd) {
  // TODO To be implemented
    //create client buffer
    net_buffer_t buffer = nb_create(fd, MAX_LINE_LENGTH);
    send_string(fd, "220 mysmtpd Simple Mail Transfer Service Ready\r\n");

    //state to indicate sequence: MAIL > RCPT > DATA
    int state = 1;

    //create the list of recipients
    user_list_t rcptList = create_user_list();

  while(1){//infinite loop

      //tempout is a buffer for user input
      char tempout[MAX_LINE_LENGTH+1];
      int rcv;
      rcv = nb_read_line(buffer,tempout);
      if(rcv > MAX_LINE_LENGTH){
          send_string(fd, "455 Server unable to accommodate parameters");
          break;
      }

      //variables to help in storing emails in a form of string
      char *st,*en;
      char *username = malloc(MAX_USERNAME_SIZE);
      char *dest = malloc(MAX_USERNAME_SIZE);

      for (int i = 0; i < 4; i++) {
          if (islower(tempout[i])) {
              tempout[i] += 'A' - 'a';
          }
      }
      //command is the command code to initiate which process is user trying to do
      char command[MAX_LINE_LENGTH+1];
      strncpy(command, tempout, 4);

      if (STREQUAL(command, "HELO")) { // Initial greeting
          send_string(fd, "250 Helo Ok\r\n");
      } else if (STREQUAL(command, "MAIL")) { // New mail from...
          st = strchr(tempout, '<'); //where the email starts
          st++;

          en = strchr(tempout, '>'); //where the email ends

          if (st == NULL || en == NULL) {
              send_string(fd, "503 Bad sequence of commands\r\n");
          } else {
              strncpy(username, st, en - st); //the email of the sender

              send_string(fd, "250 MAIL Ok \r\n");
              state = 2;
          }
      } else if (STREQUAL(command, "RCPT")) { // Mail addressed to...
          st = strchr(tempout, '<'); //where the email starts
          st++;

          en = strchr(tempout, '>'); //where the email ends

          if (st == NULL || en == NULL) {//if syntax isn't proper
              send_string(fd, "503 Bad sequence of commands\r\n");
          }else {//if it does have proper syntax
              strncpy(dest, st, en-st); //the email of the recipient

              if (is_valid_user(dest, NULL) == 0) { //if email is not in users.txt
                  send_string(fd, "550 No such user here \r\n");
              } else if (state != 2 && state != 3) { // if the user hasn't input their email from
                  send_string(fd, "555 Please input MAIL FROM:<your@email.com> \r\n");
              } else {
                  add_user_to_list(&rcptList, dest); //add this recipient to the list
                  send_string(fd, "250 Ok recipient \r\n");
                  state = 3;
              }
          }
      } else if (STREQUAL(command, "DATA")) { // Message contents...
          if(state==3) {
              send_string(fd, "354 Start mail input; end with <CRLF>.<CRLF>\r\n");

              //create temp file and save the email inside
              char tmpfname[MAX_USERNAME_SIZE] = "/tmp/mail.XXXXXX";
              int tmpfd = mkstemp(tmpfname);
              printf("Temp Filename: %s \n", tmpfname);
              FILE *fpt = fdopen(tmpfd, "w");

              while (1) {//looping until single "."
                  int data = nb_read_line(buffer, tempout);
                  if(data > MAX_LINE_LENGTH){
                      send_string(fd, "452 Requested action not taken: insufficient system storage\r\n");
                  }else {
                      fwrite(tempout, sizeof(char), strlen(tempout), fpt);

                      char dot[MAX_LINE_LENGTH]; //helper array to avoid system difference (OS or Windows or Linux)
                      dot[0] = 'x'; //initialize so it can be used again

                      if (data == 2) { //if it is only dot and CRLF
                          strncpy(dot, tempout, 1);
                      }

                      if (STREQUAL(dot, ".")) { // A single "." signifies the end
                          send_string(fd, "250 Ok\r\n");
                          //cleanup and change the state back
                          remove(dot);
                          fclose(fpt);
                          state = 1;
                          break;
                      }
                  }
              }
              //save the email
              save_user_mail(tmpfname, rcptList);
              //cleanup the temp file
              remove(tmpfname);
          }else{ //in the wrong state
              send_string(fd, "555  MAIL FROM/RCPT TO parameters not recognized or not implemented\r\n");
          }

      } else if (STREQUAL(command, "NOOP")) { // Do nothing.
          send_string(fd, "250 Ok noop\r\n");
      } else if (STREQUAL(command, "QUIT")) { // Close the connection
          //dont forget to cleanup
          remove(tempout);
          remove(command);
          free(username);
          free(dest);
          destroy_user_list(rcptList);
          send_string(fd, "221 Ok mystmpd closing transmission channel\r\n");
          break;
      } else { // The verb used hasn't been implemented.
          send_string(fd, "502 Command Not Implemented\r\n");
      }

  }
}
