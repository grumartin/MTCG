package at.fhtw.service.pckg;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.dal.repo.PackageRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Pckg;
import at.fhtw.service.card.CardController;

public class PackageController {
    private PackageRepo packageRepo;

    public PackageController() {
        this.packageRepo = new PackageRepo();
    }

    public Response handlePost(Request request){
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid");


        if(!(request.getAuthorizedClient().getUsername().equals("admin")))
            return new Response(HttpStatus.FORBIDDEN,
                    ContentType.PLAIN_TEXT,
                    "Provided user is not \"admin\"");


        UnitOfWork unitOfWork = new UnitOfWork();
        Pckg newPckg = createPackage(unitOfWork);
        if(newPckg.getP_id() == -1)
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");

        HttpStatus result = new CardController().createCards(request, newPckg.getP_id(), unitOfWork);
        String content;
        if(result == HttpStatus.CREATED){
            unitOfWork.commit();
            content = "Package and cards successfully created";
        }else{
            unitOfWork.rollback();
            content = "At least one card in the packages already exists";
        }
        unitOfWork.close();
        return new Response(result,
                            ContentType.PLAIN_TEXT,
                            content);
    }

    public Pckg createPackage(UnitOfWork unitOfWork){
        Pckg pckg = new Pckg(1, 5, "MTCG-Package");
        pckg.setP_id(this.packageRepo.addPackage(pckg, unitOfWork));
        return pckg;
    }
}
