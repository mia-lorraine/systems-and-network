#include  <stdio.h>
#include  <stdlib.h>
#include  <unistd.h>
#include  <sys/types.h>
#include  <string.h>
#include  <sys/socket.h>
#include  <netinet/in.h>
#include  <arpa/inet.h>

#include "customer.h"
#include "index.h"

/*   server.c
 *
 *   Author:   Mia Laurea
 *   Date  :   Feb 22, 2018
 *
 */

int  main()
{
    
    pid_t forkresult;
    
    int                  mysoc;
    struct  sockaddr_in  myconnector;
    int                  incomingsoc;
    int      i;
    int      fromclient;
    
    indextype index;
    customer person;
    
    int     first = 0;
    int     last = 329;
    int     middle = (first+last)/2;
    int     array[330];
    int count;
    int found = 0;
    int recordnum;
    int receivednum;
    
    
    mysoc = socket (AF_INET, SOCK_STREAM, 0);
    
    myconnector.sin_family = AF_INET;
    myconnector.sin_port   = htons(15060);
    myconnector.sin_addr.s_addr = htonl(INADDR_ANY);
    
    
    if (bind(mysoc, (struct sockaddr *)&myconnector, sizeof(myconnector)) == -1)
    {
        printf ("There is an error!\n");
        exit(EXIT_FAILURE);
    }
    
    FILE *binaryfile;
    
    binaryfile = fopen("indexfile", "rb");
    if(!binaryfile){
        printf("Error opening file! \n");
        return 1;
    }
    
    for(i = 0; i < 330; i++){
        fread(&index, sizeof(indextype),1, binaryfile);
        array[i] = index.idnumber;
    }
    
    listen (mysoc, 10);
    
    for (i = 1; i <= 5; i++)
    {
        incomingsoc = accept(mysoc, NULL, 0);

        printf ("Now serving client # %d\n", i);
        
        forkresult = fork();
        if (forkresult == 0) // this is the child
        {
            count = read(incomingsoc, (void *) &receivednum, 4);
            recordnum = receivednum;
            
            while(first <= last)
            {
                if(array[middle] == recordnum)
                {
                    found = 1;
                    break;
                }
                else if(array[middle] < recordnum)
                {
                    first = middle + 1;
                }
                else
                {
                    last = middle -1;
                }
                middle = (first+last)/2;
            }
            if(found == 0){
                person.idnumber = -1;
                write(incomingsoc, (void *) &person, sizeof(customer));
                break;
        }
	fclose(binaryfile);
            binaryfile = fopen("customerfile1", "rb");
            if(!binaryfile){
                printf("Problem with opening file..");
                return 1;
            }
            
            fseek(binaryfile, middle*sizeof(customer), SEEK_SET);
            fread(&person, (sizeof(customer)), 1, binaryfile);
            


	write(incomingsoc, (void *) &person , middle*sizeof(customer));
            
            
        } else {
            close(incomingsoc);
        }
    }
    close(mysoc);
    return  0;
}



