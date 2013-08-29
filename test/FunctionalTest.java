import org.junit.*;

import java.util.*;

import play.mvc.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

/**
 * Performs different functional tests
 * @author Martin Steiger
 */
@SuppressWarnings("javadoc")
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
