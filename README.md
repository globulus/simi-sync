# Šimi Sync - server-client cross-platform using Šimi!

Šimi Sync is a small framework that allows you to write client-side code while developing your backend!

Features:

* Develop your front and back end in [Šimi, an awesome programming language](https://github.com/globulus/simi).
* With a few annotations, the Šimi back end will transform its code to fit the client, thereby exposing both model classes and the API to be readily used by the client.
* Automatically consume the models and API on the client side without having to write any code at all!

Checkout out readmes in subprojects for more details:
* [Web](https://www.github.com/globulus/simi-sync/tree/master/web) - sample code for a Šimi backend that implements ŠimiSync.
* [Android](https://www.github.com/globulus/simi-sync/tree/master/android) - sample Android app that uses a ŠimiSync backend.
* [iOS](https://www.github.com/globulus/simi-sync/tree/master/ios) - sample iOS app that uses a ŠimiSync backend.

### How-to

1. Create a [Šimi backend](https://www.github.com/globulus/simi-sync/tree/master/web).
2. Use *SimiSyncModels.Model* annotation and its children to specify client-side models and differentiate them from server ones.
3. Use *SimiSyncControllers.ClientTask* annotation to specify endpoints that should be a part of client-side API.
4. Setup your client app to use Šimi, and invoke the *SimiSyncManager#boot* method, specify the URL of your backend, as well as the model/API version you'd like to use.
5. Utilize downloaded models and API through *ActiveSimi*.
