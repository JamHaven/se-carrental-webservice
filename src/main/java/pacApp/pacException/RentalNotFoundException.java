package pacApp.pacException;

public class RentalNotFoundException extends RuntimeException {

    public RentalNotFoundException(Long id){
        super("Could not find rental " + id.toString());
    }
}
