import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Test;

/**
 * Performs some model tests
 * @author Martin Steiger
 */
public class ModelTest 
{
    @Test
    public void findById() 
    {
        running(fakeApplication(), new Runnable() 
        {
           @Override
           public void run() 
           {
//               Computer macintosh = Computer.find.byId(21l);
//               assertThat(macintosh.name).isEqualTo("Macintosh");
//               assertThat(formatted(macintosh.introduced)).isEqualTo("1984-01-24");
           }
        });
    }
    
    @Test
    public void pagination() 
    {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() 
//        {
//           @Override
//           public void run() 
//           {
//               Page<Computer> computers = Computer.page(1, 20, "name", "ASC", "");
//               assertThat(computers.getTotalRowCount()).isEqualTo(574);
//               assertThat(computers.getList().size()).isEqualTo(20);
//           }
//        });
    }
    
}
