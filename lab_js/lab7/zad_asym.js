
var Fork = function(forkId) {
  this.state = 0;
  this.forkId = forkId;
  return this;
}

Fork.prototype.acquire = function(philosopherId, callBack) { 
  let delay = 1;

  let loop = () => {
    setTimeout(() => {
      if(this.state === 1) {
        delay *= 2;
        console.log("Philosopher (" + philosopherId + ") wait: " + delay);
        loop();
      }
      else {
        console.log(
          "Philosopher (" + philosopherId + ") get fork (" + this.forkId + ")"
        );
        this.state = 1;
        if(callBack) callBack();
      }
    }, delay);
  }

  loop();
}

Fork.prototype.release = function() { 
  this.state = 0; 
}

var Philosopher = function(id, forks) {
  this.id = id;
  this.forks = forks;
  this.f1 = id % forks.length;
  this.f2 = (id+1) % forks.length;
  return this;
}

Philosopher.prototype.startAsym = function(count) {
  let forks = this.forks,
      firstForkIndex = this.f1,
      secondForkIndex = this.f2,
      id = this.id;

  let loop = () => {
    if(count > 0) {
      console.log("loop: (id, count) (" + id + ", " + count + ")");
      let forkIndex = id % 2 === 0 ? secondForkIndex : firstForkIndex;
      forks[forkIndex].acquire(id, getNextFork);
      count--;
    }
  }

  let think = () => {
    setTimeout(() => {
      console.log("Philosopher (" + id + ") think");
      loop();
    }, Math.floor(Math.random() * 1000));
  }

  let eat = () => {
    setTimeout(() => {
      console.log("Philosopher (" + id + ") eat");
      forks[firstForkIndex].release();
      forks[secondForkIndex].release();
      think();
    }, Math.floor(Math.random() * 1000));
  }

  let getNextFork = () => {
    let forkIndex = id % 2 === 0 ? firstForkIndex : secondForkIndex;
    forks[forkIndex].acquire(id, eat);
  }

  // start
  loop();

}

// =====================================================================

var N = 5;
var forks = [];
var philosophers = []
for (var i = 0; i < N; i++) {
  forks.push(new Fork(i));
}

for (var i = 0; i < N; i++) {
  philosophers.push(new Philosopher(i, forks));
}

for (var i = 0; i < N; i++) {
  philosophers[i].startAsym(10);
}