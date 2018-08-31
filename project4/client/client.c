#include  <stdio.h>
#include  <stdlib.h>
#include  <unistd.h>
#include  <sys/types.h>
#include  <string.h>
#include  <sys/socket.h>
#include  <netinet/in.h>
#include  <arpa/inet.h>
#include "customer1.h"
#include "index1.h"

/*  client.c
 *
 *  Mia Laurea
 *  Feb. 22, 2018
 *  Project 4
 *
 */


int  main()
{
    customer person;
    
    char    line_from_server[100];
    int     count;
    int     status;
    int     num, num2, num3;
    int k, i;
    
    int                  work_socket;
    struct  sockaddr_in  server_port;
    
    work_socket  = socket (AF_INET, SOCK_STREAM, 0);
    
    server_port.sin_family = AF_INET;
    server_port.sin_port   = htons(15060);
    server_port.sin_addr.s_addr = inet_addr("10.15.14.26");
    
    status = connect (work_socket, (struct sockaddr *)&server_port,
                      sizeof(server_port));
    if (status == -1 )
    {
        // print error message here!
        printf("This connection is not available. \n");
        
        exit(-1);
    }
    //Test : 1127234 6705923 2295800
    
    printf("Enter a record number: \n");
    scanf ("%d", &num);
    write (work_socket, (void *) &num, sizeof(int));

    printf("Enter a second record number: \n");
    scanf ("%d", &num2);
    write (work_socket, (void *) &num2, sizeof(int));
  
    printf("Enter a third record number: \n");
    scanf ("%d", &num3);
    write (work_socket, (void *) &num3, sizeof(int));
    
    shutdown (work_socket, 1);
    
    printf ("\n **************************** \n\n");
    
    if(person.idnumber == -1){
        printf("Record number could not be found.. \n");
        
    }
    
    count = read( work_socket , (void *) &person, sizeof(person));
    while(count > 0){
  
         printf("%d %-13s %-20s %-22s %-16s %-21s %-12s %8.2f \n ", person.idnumber, person.name, person.membership, person.login, person.phone,person.city, person.ssn, person.balance);
        
       count = read( work_socket , (void *) &person, sizeof(person));
        
       }
    close(work_socket);
    return 0;
    
}


