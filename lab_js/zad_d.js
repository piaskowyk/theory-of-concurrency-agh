
function printAsync(s, cb) {
  var delay = Math.floor((Math.random()*1000)+500);
  setTimeout(function() {
      console.log(s);
      if (cb) cb();
  }, delay);
}

// Napisz funkcje (bez korzytania z biblioteki async) wykonujaca 
// rownolegle funkcje znajdujace sie w tablicy 
// parallel_functions. Po zakonczeniu wszystkich funkcji
// uruchamia sie funkcja final_function. Wskazowka:  zastosowc 
// licznik zliczajacy wywolania funkcji rownoleglych 


function inparallel(parallel_functions, final_function) {
  let counter = parallel_functions.length;
  decrement = () => { 
    counter--; 
    if(counter == 0) {
      final_function();
    }
  }
  
  for(i = 0; i < parallel_functions.length; i++) {
    parallel_functions[i](decrement);
  }
}

A=function(cb){printAsync("A",cb);}
B=function(cb){printAsync("B",cb);}
C=function(cb){printAsync("C",cb);}
D=function(cb){printAsync("Done",cb);}

inparallel([A,B,C],D) 

// kolejnosc: A, B, C - dowolna, na koncu zawsze "Done" 