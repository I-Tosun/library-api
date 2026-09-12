package nl.novi.boekenbeheer.util;

import nl.novi.boekenbeheer.exception.BadRequestException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

//Hulpklasse voor het opslaan en ophalen van geüploade bestanden
@Component
public class FileStorageUtil {
    // Alleen deze bestandstypen zijn toegestaan
    private static final List<String> TOEGESTANE_EXTENSIES = List.of(".jpg", ".jpeg", ".png", ".pdf");

    private final Path uploadDir;

    public FileStorageUtil(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // Upload-map aanmaken als die nog niet bestaat
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Kon de upload map niet aanmaken: " + e.getMessage());
        }
    }
    // Slaat een geüpload bestand op met een UUID-bestandsnaam, valideert extensie en beschermt tegen path traversal
    public String slaBestandOp(MultipartFile bestand) {
        // Controle 1: bestand mag niet leeg zijn
        if (bestand.isEmpty()) {
            throw new BadRequestException("Bestand is leeg");
        }

        String origineleNaam = bestand.getOriginalFilename();
        if (origineleNaam == null || origineleNaam.isBlank()) {
            throw new BadRequestException("Bestandsnaam is ongeldig");
        }
        // Controle 2: bestand moet een geldige extensie hebben
        int puntIndex = origineleNaam.lastIndexOf(".");
        if (puntIndex < 0 || puntIndex == origineleNaam.length() - 1) {
            throw new BadRequestException("Bestand heeft geen geldige extensie");
        }

        String extensie = origineleNaam.substring(puntIndex).toLowerCase();
        if (!TOEGESTANE_EXTENSIES.contains(extensie)) {
            throw new BadRequestException("Bestandstype niet toegestaan. Toegestaan: " + TOEGESTANE_EXTENSIES);
        }
        // UUID voorkomt naamconflicten en maakt bestandsnaam niet beïnvloedbaar
        String nieuweNaam = UUID.randomUUID() + extensie;

        try {
            Path doelpad = this.uploadDir.resolve(nieuweNaam).normalize();

            // Controle 3: path traversal bescherming
            if (!doelpad.startsWith(this.uploadDir)) {
                throw new BadRequestException("Ongeldige bestandsnaam");
            }
            Files.copy(bestand.getInputStream(), doelpad);
            return nieuweNaam;
        } catch (IOException e) {
            throw new RuntimeException("Kon het bestand niet opslaan: " + e.getMessage());
        }
    }

    // Laadt een opgeslagen bestand op als Resource voor downloaden
    public Resource laadBestand(String bestandsnaam) {
        try {
            Path bestandspad = this.uploadDir.resolve(bestandsnaam).normalize();

            // Path traversal bescherming bij downloaden
            if (!bestandspad.startsWith(this.uploadDir)) {
                throw new BadRequestException("Ongeldige bestandsnaam");
            }
            Resource resource = new UrlResource(bestandspad.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RecordNotFoundException("Bestand niet gevonden: " + bestandsnaam);
            }
        } catch (MalformedURLException e) {
            throw new RecordNotFoundException("Bestand niet gevonden: " + bestandsnaam);
        }
    }
}