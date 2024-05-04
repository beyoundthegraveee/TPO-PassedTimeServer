/**
 *
 *  @author Kurzau Kiryl S24911
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.*;

public class ClientTask extends FutureTask<String> {
    public ClientTask(Callable<String> callable) {
        super(callable);
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        StringBuffer stringBuffer = new StringBuffer();
        return new ClientTask(()->{
            c.connect();
            c.send("login " + c.getId());
            reqList.forEach((req) -> {
                String str = c.send(req);
                if (showRes){
                    System.out.println(str);
                }
            });
            stringBuffer.append(c.send("bye and log transfer"));
            return stringBuffer.toString();
        });
    }
}
