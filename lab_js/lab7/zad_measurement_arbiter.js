const fs = require('fs');

var Arbiter = function(N) {
  this.freePlace = N - 1;
  return this;
}

Arbiter.prototype.getAccesToTable = function(philosopherId, callback) {
  let delay = 1;

  let loop = () => {
    setTimeout(() => {
      waitingCounter += delay;
      if(this.freePlace === 0) {
        delay *= 2;
        if(delay > 2048) delay = 1;
        console.log("Philosopher (" + philosopherId + ") blocked by Arbiter, wait: " + delay);
        loop();
      }
      else {
        console.log(
          "Philosopher (" + philosopherId + ") get access to table."
        );
        this.freePlace--;
        if(callback) callback();
      }
    }, delay);
  }

  loop();
}

Arbiter.prototype.leaveTable = function(philosopherId) {
  console.log("Philosopher (" + philosopherId + ") leave table.");
  this.freePlace++;
}

var Fork = function(forkId) {
  this.state = 0;
  this.forkId = forkId;
  return this;
}

Fork.prototype.acquire = function(philosopherId, callback) { 
  let delay = 1;

  let loop = () => {
    setTimeout(() => {
      waitingCounter += delay;
      if(this.state === 1) {
        delay *= 2;
        if(delay > 2048) delay = 1;
        console.log("Philosopher (" + philosopherId + ") wait: " + delay);
        loop();
      }
      else {
        console.log(
          "Philosopher (" + philosopherId + ") get fork (" + this.forkId + ")"
        );
        this.state = 1;
        if(callback) callback();
      }
    }, delay);
  }

  loop();
}

Fork.prototype.release = function() { 
  this.state = 0; 
}

var Philosopher = function(id, forks, arbiter) {
  this.id = id;
  this.forks = forks;
  this.f1 = id % forks.length;
  this.f2 = (id+1) % forks.length;
  this.arbiter = arbiter;
  return this;
}

Philosopher.prototype.startConductor = function(count) {
  let forks = this.forks,
      firstForkIndex = this.f1,
      secondForkIndex = this.f2,
      id = this.id,
      arbiter = this.arbiter;

  let loop = () => {
    if(count > 0) {
      console.log("loop: (id, count) (" + id + ", " + count + ")");
      arbiter.getAccesToTable(id, getFirstFork);
      count--;
    } 
    else {
      runing--;
    }
  }

  let think = () => {
    setTimeout(() => {
      console.log("Philosopher (" + id + ") think");
      loop();
    }, Math.floor(Math.random() * 100));
  }

  let eat = () => {
    setTimeout(() => {
      console.log("Philosopher (" + id + ") eat");
      forks[firstForkIndex].release();
      forks[secondForkIndex].release();
      arbiter.leaveTable(id);
      think();
    }, Math.floor(Math.random() * 100));
  }

  let getFirstFork = () => {
    forks[firstForkIndex].acquire(id, getSecondFork);
  }

  let getSecondFork = () => {
    forks[secondForkIndex].acquire(id, eat);
  }

  // start
  loop();

}

// =====================================================================

let N_List = [5, 10, 15, 20, 25, 30];
let waitingCounter = 0;
var N;
let runing = 0;

let testArbiter = (index) => {
  waitingCounter = 0;
  N = N_List[index];
  runing = N;
  var forks = [];
  var philosophers = [];

  let arbiter = new Arbiter(N);
  for (var i = 0; i < N; i++) {
    forks.push(new Fork(i));
  }
  
  for (var i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks, arbiter));
  }
  
  for (var i = 0; i < N; i++) {
    philosophers[i].startConductor(10);
  }
}

let counter = 0;
let loopTestArbiter = () => {
  setTimeout(() => {
    if(runing == 0) {
      if(runing == 0 && counter > 0) {
        fs.appendFileSync(
          'data/mesurement_arbiter', 
          "N: " + N + ", waiting: " + waitingCounter + " ms, avg: " + waitingCounter/N + " ms\n"
        );
        fs.appendFileSync(
          'data/mesurement_arbiter_data', 
          N + " " + waitingCounter + " " + waitingCounter/N + "\n"
        );
      }

      testArbiter(counter);
      counter++;
    }

    if(counter <= N_List.length) {
      loopTestArbiter();
    }
  }, 1000);
}

fs.appendFileSync('data/mesurement_arbiter', "Arbiter algorythm: \n");
loopTestArbiter();

