#include <stdio.h>
#include <strings.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>

#define MAX_BABY_BIRDS 5
#define MAX_WORMS 20
int worms = 10;
sem_t Eatsemaphore;
sem_t Fillsemaphore;
//Function Prototypes
void* consume(void* arg);
void* produce();

int main(int argc, int* argv){

    pthread_t baby_birds[MAX_BABY_BIRDS];
    pthread_t mama_bird[1];

    sem_init(&Eatsemaphore, 0, 1);
    sem_init(&Fillsemaphore, 0, 0);

    for(int i = 0; i < MAX_BABY_BIRDS; i++){
        pthread_create(&baby_birds[i], NULL, consume, (void*)(i+1));
    }
    pthread_create(&mama_bird[1], NULL, produce, NULL); 
    
    for(int i = 0; i < MAX_BABY_BIRDS; i++){
        pthread_join(baby_birds[i], NULL);
    }

    pthread_join(mama_bird[1], NULL);
    sem_destroy(&Eatsemaphore);
    sem_destroy(&Fillsemaphore);

    return 0;
}

void* consume(void* arg){
    int bird_ID = (int) arg;
    while(1){
        sem_wait(&Eatsemaphore);

        if(worms > 0){
            int worms_left = --worms;
            printf("Baby bird #%ld ate a worm, %d left\n", bird_ID, worms_left);
            sleep(1);
            sem_post(&Eatsemaphore);
        }
        else{
            printf("Baby bird #%d is chirping\n", bird_ID);
            sem_post(&Fillsemaphore);
            sleep(1);
        }

    }
}
void* produce(){
    while(1){
        sem_wait(&Fillsemaphore);
        if(worms == 0){
            srand(time(NULL));
            worms = rand() % MAX_WORMS + 1;
            printf("Mama bird gathered %d worms\n", worms);
            sleep(1);
        }
        sem_post(&Eatsemaphore);
    }
}