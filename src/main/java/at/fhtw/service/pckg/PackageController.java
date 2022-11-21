package at.fhtw.service.pckg;

import at.fhtw.dal.repo.PackageRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Pckg;
import at.fhtw.service.card.CardController;

import static at.fhtw.service.Service.unitOfWork;

public class PackageController {
    private PackageRepo packageRepo;

    public PackageController() {
        this.packageRepo = new PackageRepo();
    }

    public Response handlePost(Request request){
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "");


        if(!(request.getAuthorizedClient().getUsername().equals("admin")))
            return new Response(HttpStatus.FORBIDDEN,
                    ContentType.PLAIN_TEXT,
                    "");


        Pckg newPckg = createPackage();
        if(newPckg.getP_id() == -1)
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");

        HttpStatus result = new CardController().createCards(request, newPckg.getP_id());
        if(result == HttpStatus.CREATED){
            unitOfWork.commit();
        }else{
            unitOfWork.rollback();
        }

        return new Response(result,
                            ContentType.PLAIN_TEXT,
                            "");
    }

    public Pckg createPackage(){
        Pckg pckg = new Pckg(1, 5, "MTCG-Package");
        pckg.setP_id(this.packageRepo.addPackage(pckg, unitOfWork));
        return pckg;
    }
}
