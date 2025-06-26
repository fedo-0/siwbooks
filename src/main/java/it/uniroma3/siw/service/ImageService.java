package it.uniroma3.siw.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

	public List<String> salvaImmagini(List<MultipartFile> images, String fold) throws IOException {
		List<String> savedPaths = new ArrayList<>();
		if (images == null)
			return savedPaths;
		Path folder = Paths.get("uploads/images/" + fold);
		Files.createDirectories(folder); // crea se non esiste

		for (MultipartFile file : images) {
			if (!file.isEmpty()) {
				String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
				Path target = folder.resolve(filename);
				file.transferTo(target);

				savedPaths.add("/images/" + fold + "/" + filename);
			}
		}
		return savedPaths;
	}
	
	public void eliminaImmagini(List<String> reviewImages) throws IOException {
		if (reviewImages == null)
			return;
		for (String pathRelativo : reviewImages) {
			Path pathAssoluto = Paths.get("uploads" + pathRelativo);
			Files.deleteIfExists(pathAssoluto); // Non lancia errore se non esiste
		}
	}
	
	public String salvaImmagine(MultipartFile image, String fold) throws IOException {
		if (image==null || image.isEmpty())  return null;
		
		Path folder = Paths.get("uploads/images/" + fold);
		Files.createDirectories(folder); // crea la cartella se non esiste

		String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
		Path target = folder.resolve(filename);
		image.transferTo(target);

		return "/images/" + fold + "/" + filename;
	}
	
	public void eliminaImmagine(String image) throws IOException {
		if (image == null)
			return;
		Path pathAssoluto = Paths.get("uploads" + image);
		Files.deleteIfExists(pathAssoluto); // Non lancia errore se non esiste
	}

}
