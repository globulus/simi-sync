# Šimi Sync Web - build a backend in Šimi!

[Šimi](https://github.com/globulus/simi) can be used to write backends as well! In this sample project you'll find everything you need to set up a Šimi-based backend that also supports [Šimi sync](#simi-sync).

### Spring Boot

The Šimi backend framework sits on top of [Spring Boot](http://spring.io/projects/spring-boot) and uses it to bootstrap itself into existence.

The basic setup is as follows:
1. Add *simi.jar* as a dependency of your project.
2. Add your Šimi Stdlib, libraries and source files to the resources/static folder. Alongside Stdlib, required Šimi source files are *SimiSyncCommons.simi, SimiSyncControllers.simi, SimiSyncImports.simi,* and *SimiSyncModels.simi*.
3. Set your SpringBootApplication file to load Šimi and ŠimiSync, as demonstrated in **SimiSyncApplication.java**.
4. Set up a universal RestController that will route incoming requests into Šimi and return responses from it, as shown in **SimiSyncController.java**.

And that's it! You're ready to start writing your Šimi backend.

### Controllers

Šimi controllers allow you to add RESTful API to your backend. All controller files must live in the *controllers* folder in resources/static, or its subfolders. When your backend is loaded, ŠimiSyncControllers class will run its parseControllers() method and build a routing map (this is done automatically if your SpringBootApplication file is configured properly).

A controller is a regular Šimi class that contains at least one method annotated with **SimiSyncControllers.Route** annotation. The Route annotation exposes a REST endpoint, and makes sure that a request sent to that endpoint will end up in the annotated method, and its return value send back as a response. The method is required to take up exactly one parameter of type **SimiSyncControllers.Request**.

The Route annotation takes two String parameters: endpoint and verb. Endpoint should be prefixed with a slash, while the verb should be uppercased. Endpoints support paths, with path components starting with $, such as "/beers/$guid/details". SimiSyncControllers router will properly route matching requests, storing path values inside the request.path object.

You should return a regular SimiValue from your function. There is no need to specifically serialize the return value using the *ivic* operator. All responses are automatically associated with HTTP code 200 (OK). If you wish to return a different code, have your function return an object with two keys, *code* and *body*.

Your API can be used both with *Šimi and JSON clients*. The default request and response payload type is Šimi code, i.e the router will use *gu* with request body, and invoke the response's body with *ivic*. However, if the request has *application/json* in either Accept or Content-Type headers, the router will treat both payloads as JSON. This naturally puts some limitations on what can be sent around, as JSON is inferior to Šimi code.

A sample controller class containing a single route method can be found below:

```ruby
import "./SimiSyncControllers.simi"
import "./db/DbHelper.simi"
import "./models/User.simi"

class LoginController:
    !SimiSyncControllers.Route("/login", "POST")
   def login(request):
           credentials = request.body
           user = DbHelper.orm().query(User).where("email = '\(credentials.email)'").first()
           if user:
               if user.password == credentials.password: return user.cookie
               else: return [code = 401, body = "Invalid password"]
           end
           else:
               signup = User(guid(), credentials.email, credentials.password)
               signup.updateCookie()
               DbHelper.orm().upsert(signup)
               return [code = 201, body = signup.cookie]
           end
    end
end
```

#### Rendering

Šimi controllers can return any text using [SMT](https://github.com/globulus/simi#smt). Most of the time, this is used for server-side rendering of Web pages.

A controller that may render a web page should be annotated with the **SimiSyncControllers.Render**. It may or may not receive a path to the template file. If no template file is specified, the system will look for one in the **views** folder (e.g, if you controller is named HtmlController, its corresponding view is HtmlView.html.smt). The template will be compiled during server boot.

Once a rendering controller is invoked, should it reach the end of its code without returning, it will invoke its SMT's *run* function, and return its response.

As an example, consider the following controller that prints out its request's headers as an HTML table. It has a controller code:
```ruby
import "./SimiSyncControllers.simi"

class HtmlController:
    !SimiSyncControllers.Route("/headers", "GET")
    !SimiSyncControllers.Render()
    def renderHeaders(request): pass
end
```
and an HTML SMT view file:
```html
 <html>
    <body>
        <h3>Incoming headers (without cookie):</h3>
        <table>
            <tr>
                <th>Key</th>
                <th>Value</th>
            </tr>
            %%for [key, value] in request.headers.zip():
                %%if key != "cookie":
                    <tr>
                        <td><%=key%></td>
                        <td><%=value%></td>
                    </tr>
                %%end
            %%end
        </table>

        <h5>Cookie is: <%=request.headers.cookie%></h5>
    <body>
</html>
```

#### Debugging

You can use Šimi Debugger with Šimi Web by enabling ActiveSimi's *setDebug()* method and supplying a debugger interface. You may opt to use a default *ConsoleInterface*, or a visual *BrowserInterface*.

To use BrowserInterface, navigate to *YOURDOMAIN/simi/debug*, where you'll be presented with the visual debugger. Once a breakpoint or exception hits, refresh the page if it didn't interact with the debugger yet. You can futher extend the browser interface functionality by playing around with *simi_debug.html*, and the debugger part in *SimiSyncController.java*.


### Šimi Sync

Šimi sync allows you to share your backend code with the clients to avoid having client-side write models and API tasks based on backend specs - instead, the client will invoke the /sync endpoint and download Šimi files containing the autogenerated code.

There are two parts to Šimi Sync backend - models and client tasks.

#### Models

Model files that are shared between server and client should live in the *models* folder in resources/static, or its subfolders. Their classes should be annotated with **SimiSyncModels.Model**. You can specify which fields and methods are going to be a part of client-side models package and how are they going to behave using the **Field** and **Method** annotations, respectively.

During server boot, SimiSyncModels' parse() method will analyize all model files and convert them to client models. It will also add functions to map server models to their client conterparts, and vice versa. The client shoud always send its models, and it will also receive client models exclusively - it's up to the server to do the conversions.

```ruby
import "./SimiSyncModels.simi"

!SimiSyncModels.Model(1)
class$ User:
    !SimiSyncModels.Field()
    guid = 0

    !SimiSyncModels.Field()
    email = 0

    # Password isn't being sent back to the client nor is received from it
    !SimiSyncModels.Field(true, 1)
    password = 0

    # And neither is cookie
    !SimiSyncModels.Field(true, 1)
    cookie = 0

    !SimiSyncModels.Method() # This constructor is the only one visible at client-side
    def init(email is String, password is String): pass
    def init(email is String): pass
    def init(guid is String, email is String, password is String): pass

    def updateCookie(): @cookie = guid()

end
```

#### Client Tasks

A client task is a Route annotated method that has a client-side equivalent. Alongside the Route annotation, this method should have **SimiSyncController.ClientTask** endpoint. Let's take the LoginController example above and expand it to include a client task:

```ruby
...
class LoginController:
    !SimiSyncControllers.Route("/login", "POST")
    !SimiSyncControllers.ClientTask(1, [User], nil, nil, [200, 201]) # And that's it!
    def login(request):
        ...
    end
end

...

class BeerController:
    !SimiSyncControllers.Route("/beers/$guid", "DELETE")
    !SimiSyncControllers.ClientTask(1, [String], ["simiCookie"], nil, nil)
    def delete(request):
       ...
    end
end
```

When your server is booted, SimiSyncControllers class will autogenerate a *ClientTasks.simi* file that will contain cient-side code based on ClientTask annotation specs:
```ruby
class$ ClientTasks:
    def postLogin(body is User, success is Function, error is Function):
       url = SimiSync.baseUrl + "/login"
       response = yield Net.post([url = url, json = ivic body, headers = []])
       if response.code in [200, 201]: success(response)
       else: error(response)
    end

    def deleteBeers(guid is String, simiCookie is String, success is Function, error is Function):
       url = SimiSync.baseUrl + "/beers/$guid".replacing("$guid", guid)
       response = yield Net.delete([url = url, json = ivic body, headers = [simiCookie=simiCookie]])
       if response.code in [200]: success(response)
       else: error(response)
    end
end
```

* Client-side methods's names are composed of verb + first part of endpoint, i.e method at endpoint */login* receiving a *POST* becomes *postLogin*.
* Client-side code is expected to use Net.simi for its networking.
* Headers, body and query parameters, as well as their type verification, are added based on the ClientTask annotation params.
* Each client task method will two callbacks, *success* and *error*.

#### Sync Endpoint

Using Šimi Sync requires you to place **SyncController.simi** in your controllers folder. This controller exposes a single endpoint that is used by clients to fetch models and client-tasks code. This file should not be changed.
