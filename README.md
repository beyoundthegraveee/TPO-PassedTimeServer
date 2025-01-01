PASSTIME_SERVER task

The server (Server class) provides information about the passing time. Clients (Client class): a) connect to the server b) send requests

For each client, the server keeps a log of its queries and their results, as well as a general log of all requests from all clients. Logs are executed in the server's internal memory (outside the file system).

Protocol

Request Response Example login id logged in login Adam dataFrom dataTo description of time elapsed according to specification from S_PASSTIME 2019-01-20 2020-04-01
bye logged out bye and log transfer client log content in sample printout from Main class operation

Server class structure

construct: : public Server(String host, int port)

Required methods: method: public void startServer() - starts the server in a separate thread, method: public void stopServer() - stops the server and the thread in which it is running method: String getServerLog() - returns a general server log Construction requirements for the Server class multiplexing socket channels (use of selector). the server can handle many clients in parallel, but the client requests are handled in a single thread Client class structure

constructor: public Client(String host, int port, String id), where id - client identifier

Required methods: method: public void connect() - connects to the server method: public String send(String req) - sends a request req and returns the server's response Design requirements for the Client class non-blocking input - output

Additionally, create a ClientTask class that allows clients to be launched in separate threads via ExecutorService. Objects of this class are created by the static method:

public static ClientTask create(Client c, List<String> reqs, boolean showSendRes)
where: c - client (Client class object) reqs - list of queries about the passage of time

The code running in the thread should perform the following actions: connects to the server, sends a "login" request with the client identifier sends subsequent requests from the reqs list sends "bye and log transfer" and receives a log of queries and their results for a given client from the server. If the showSendRes parameter is true, after each send the server's response is printed to the console. Regardless of the parameter value, it should be ensured that the client log is available as soon as the client finishes working.

Additionally, provide the Time classes (time calculation logic) and Tools (loading client options and requests needed for the Main class to work).

The prepared Main class illustrates cases of client-server interaction:

package zad1;

import java.util.; import java.util.concurrent.;

public class Main {

public static void main(String[] args) throws Exception { String fileName = System.getProperty("user.home") + "/PassTimeServerOptions.yaml"; Options opts = Tools.createOptionsFromYaml(fileName); String host = opts.getHost(); int port = opts.getPort(); boolean concur = opts.isConcurMode(); boolean showRes = opts.isShowSendRes(); Map<String, List> clRequests = opts.getClientsMap(); ExecutorService es = Executors.newCachedThreadPool(); List ctasks = new ArrayList<>(); List clogs = new ArrayList<>();

Server s = new Server(host, port);
s.startServer();

// start clients
clRequests.forEach( (id, reqList) -> {
 Client c = new Client(host, port, id);
 if (concur) {
 ClientTask ctask = ClientTask.create(c, reqList, showRes);
 ctasks.add(ctask);
 es.execute(ctask);
 } else {
 c.connect();
 c.send("login" + id);
 for(String req : reqList) {
 String res = c.send(req);
 if (showRes) System.out.println(res);
 }
 String clog = c.send("bye and log transfer");
 System.out.println(clog);
 }
});

if (concur) {
ctasks.forEach( task -> {
try {
String log = task.get();
clogs.add(log);
} catch (InterruptedException | ExecutionException exc) {
System.out.println(exc);
}
});
clogs.forEach( System.out::println);
es.shutdown();
}
s.stopServer();
System.out.println("\n=== Server log ===");
System.out.println(s.getServerLog());
}

}

Console results A. Contents of PassTimeServerOptions.yaml file

host: localhost port: 7777 concurMode: false # are clients running in parallel? showSendRes: false # whether to show the results of the send(...) method returned by the server clientsMap: # client_id -> list of requests Asia: - 2019-01-10 2020-03-01 - 2020-03-27T10:00 2020-03-28T10:00 Adam: - 2018-01-01 2020-03-27 - 2020-03-28T10:00 2020-03-29T10:00

A. Result: === Asia log start === logged in Request: 2019-01-10 2020-03-01 Result: From January 10, 2019 (Thursday) to March 1, 2020 (Sunday)

passes: 416 days, weeks 59.43
calendar: 1 year, 1 month, 20 days Request: 2020-03-27T10:00 2020-03-28T10:00 Result: From March 27, 2020 (Friday) 10:00 AM to March 28, 2020 (Saturday) 10:00 AM 10:00
passes: 1 day, weeks 0.14
hours: 24, minutes: 1440
calendar: 1 day logged out === Asia log end ===
=== Adam log start === logged in Request: 2018-01-01 2020-03-27 Result: From January 1, 2018 (Monday) to March 27, 2020 (Friday)

passes: 816 days, weeks 116.57
calendar: 2 years, 2 months
