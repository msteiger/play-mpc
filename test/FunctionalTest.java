import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import org.junit.Test;

import play.mvc.Result;

/**
 * Performs different functional tests
 * @author Martin Steiger
 */
public class FunctionalTest 
{
    @Test
    public void redirectHomePage() 
    {
        running(fakeApplication(), new Runnable() 
        {
           @Override
           public void run() 
           {
               Result result = callAction(controllers.routes.ref.Application.index());

               assertThat(status(result)).isEqualTo(SEE_OTHER);
               assertThat(redirectLocation(result)).isEqualTo("/playlist");
           }
        });
    }
    
    @Test
    public void listComputersOnTheFirstPage() 
    {
        running(fakeApplication(), new Runnable() 
        {
           @Override
           public void run() 
           {
               Result result = callAction(controllers.routes.ref.Application.playlist(0));

               assertThat(status(result)).isEqualTo(OK);
//               assertThat(contentAsString(result)).contains("574 computers found");
           }
        });
    }
    
 
}
