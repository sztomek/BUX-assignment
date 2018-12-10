# BUX-assignment

This repository contains my solution for the assignment.
The application is very simple, only has 2 screens.

## Product Select screen
This is the landing page of the app.  

It displays a `Spinner` with the available trading products.  
Pressing the 'Show details' button will take us to the details screen

![select product](https://cdn.pbrd.co/images/HR3Uvdz.png "Select Product screen")
![select_product_dropdown](https://cdn.pbrd.co/images/HR3UNJv.png "Select Product with expanded spinner")


## Product Details screen
This screen displays the details of the product.  

It initiates a REST request to get the details. After that you can enable/disable live price updates via WebSocket.  

![details](https://cdn.pbrd.co/images/HR3SQxA.png "Product Details screen")
![details_live](https://cdn.pbrd.co/images/HR3TwT6.png "Product Details screen with price updates enabled")

*Please note that I didn't manage to make the beta env work, I always get HTTP 400 as a response. Currently `OkHttp` is used for WebSocket communication, but I have even tried `AsyncAndroid` -- had the same result.  
If you want to try out the app, please change the `wsUrl` value in the `DataModule.kt` to use the local address of the mock server.*

**You can also check out [this video](https://drive.google.com/open?id=1MDlpJ3t26R0ws_zoY4RDR-Ipd3P0dqjV) to see how it works with the mock server.**

## About
The app is written in `Kotlin`.  

It follows the principles of CLEAN architecture design pattern. For the presentation layer MVVM is being used. Unidirectional data flow is being employed.

The key players are: `ViewModels`, `Interactors`, `Repositories`.

### ViewModel
Extends `androidx.lifecycle.viewmodel.ViewModel`. 

Publishes `UiState` changes via `LiveData` -- observers like `Activities or Fragments` can observe the changes and react accordingly.

Can take `Action`s with the necessary params to trigger changes on the current `UiState`. An `Action` produces `Operations`. A new state is produced according to this recipe: *new `UiState` = <old `UiState`> + `Operation`*

### Interactor
Communicates with the `Repository`. 

Has only one method which takes an `Action`. The method's return type is `Flowable`.

### Repository
Abstraction over the actual data accessors (APIs, database, etc...). Returns reactive components (`Singles`, `Completables`, `Flowables`). Operates on domain entities only.

## Used libraries
- OkHttp
- Retrofit
- Dagger
- androidx.lifecycle
- timber
- junit
- espresso
- mockito

## Tests
I have written some unit tests and integration tests too.
