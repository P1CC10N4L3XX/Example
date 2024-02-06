/*
 *    Copyright (C) 2023 Guglielmo De Angelis (a.k.a. Gulyx)
 *    
 *    This file is part of the contents developed for the course
 * 	  ISPW (A.Y. 2023-2024) at Università di Tor Vergata in Rome. 
 *
 *    This is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as 
 *    published by the Free Software Foundation, either version 3 of the 
 *    License, or (at your option) any later version.
 *
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this source.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.uniroma2.dicii.ispw.jdbcExamples.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Files;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import it.uniroma2.dicii.ispw.jdbcExamples.entities.Album;
import it.uniroma2.dicii.ispw.jdbcExamples.exceptions.DuplicatedRecordException;

public class AlbumDAOCSV implements AlbumDAO {

	private static final String CSV_FILE_NAME = "localDBFile.csv";

	private File fd;

	private HashMap<String, Album> localCache;

	public AlbumDAOCSV() throws IOException {
		this.fd = new File(CSV_FILE_NAME);

		if (!fd.exists()) {
			fd.createNewFile();
		}

		this.localCache = new HashMap<String, Album>();
	}

	@Override
	public List<Album> retreiveByAlbumName(String albumName) throws Exception {

		List<Album> lst = new ArrayList<Album>();

		synchronized (this.localCache) {
			for (String id : this.localCache.keySet()) {
				Album recordInCache = this.localCache.get(id);
				boolean recordFound = recordInCache.getTitolo().equals(albumName);
				if (recordFound) {
					lst.add(recordInCache);
				}
			}
		}

		if (lst.isEmpty()) {
			lst = retreiveByAlbumName(this.fd, albumName);
			synchronized (this.localCache) {
				for (Album album : lst) {
					this.localCache.put(String.valueOf(album.getAlbumId()), album);
				}
			}
		}

		return lst;
	}

	private static synchronized List<Album> retreiveByAlbumName(File fd, String albumName) throws Exception {
		// create csvReader object passing file reader as a parameter
		CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
		String[] record;

		List<Album> albumList = new ArrayList<Album>();

		while ((record = csvReader.readNext()) != null) {
			int posTitolo = AlbumAttributesOrder.getIndex_Titolo();

			boolean recordFound = record[posTitolo].equals(albumName);
			if (recordFound) {
				int albumId = Integer.valueOf(record[AlbumAttributesOrder.getIndex_AlbumID()]);
				String artista = record[AlbumAttributesOrder.getIndex_Artista()];
				String titolo = record[AlbumAttributesOrder.getIndex_Titolo()];
				int anno = Integer.valueOf(record[AlbumAttributesOrder.getIndex_Anno()]);

				Album album = new Album(albumId, artista, titolo, anno);
				albumList.add(album);
			}
		}

		csvReader.close();

		if (albumList.isEmpty()) {
			Exception e = new Exception("No Album Found matching with name: " + albumName);
			throw e;
		}

		return albumList;
	}

	private static synchronized List<Album> retreiveById(File fd, int albumId) throws Exception {
		// create csvReader object passing file reader as a parameter
		CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
		String[] record;

		List<Album> albumList = new ArrayList<Album>();

		while ((record = csvReader.readNext()) != null) {
			int posTitolo = AlbumAttributesOrder.getIndex_AlbumID();

			boolean recordFound = record[posTitolo].equals(String.valueOf(albumId));
			if (recordFound) {
				int id = Integer.valueOf(record[AlbumAttributesOrder.getIndex_AlbumID()]);
				String artista = record[AlbumAttributesOrder.getIndex_Artista()];
				String titolo = record[AlbumAttributesOrder.getIndex_Titolo()];
				int anno = Integer.valueOf(record[AlbumAttributesOrder.getIndex_Anno()]);

				Album album = new Album(id, artista, titolo, anno);
				albumList.add(album);
			}
		}

		csvReader.close();

		if (albumList.isEmpty()) {
			Exception e = new Exception("No Album Found matching with ID: " + albumId);
			throw e;
		}

		return albumList;
	}

	@Override
	public void saveAlbum(Album instance) throws Exception {
		boolean duplicatedRecordId = false;

		synchronized (this.localCache) {
			duplicatedRecordId = (this.localCache.get(String.valueOf(instance.getAlbumId())) != null);
		}
		
		if (!duplicatedRecordId) {
			try {
				List<Album> albumList = retreiveById(this.fd, instance.getAlbumId());
				duplicatedRecordId = (albumList.size() != 0);
			} catch (Exception e) {
				duplicatedRecordId = false;
			}
		}

		if (duplicatedRecordId) {
			DuplicatedRecordException e = new DuplicatedRecordException(
					"Duplicated Instance ID. Id " + instance.getAlbumId() + " was already assigned");
			throw e;
		}
		
		saveAlbum(this.fd, instance);
	}

	private static synchronized void saveAlbum(File fd, Album instance) throws Exception {

		// create csvWriter object passing file reader as a parameter
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)));

		String[] record = new String[4];

		record[AlbumAttributesOrder.getIndex_AlbumID()] = String.valueOf(instance.getAlbumId());
		record[AlbumAttributesOrder.getIndex_Titolo()] = instance.getTitolo();
		record[AlbumAttributesOrder.getIndex_Artista()] = instance.getArtista();
		record[AlbumAttributesOrder.getIndex_Anno()] = String.valueOf(instance.getAnno());

		csvWriter.writeNext(record);
		csvWriter.flush();
		csvWriter.close();
	}

	@Override
	public void removeAlbumById(Album instance) throws Exception {
		synchronized (this.localCache) {
			this.localCache.remove(String.valueOf(instance.getAlbumId()));
		}
		removeAlbumById(this.fd, instance);
	}

	private static synchronized void removeAlbumById(File fd, Album instance) throws Exception {
		File tmpFD = File.createTempFile("dao", "tmp");
		boolean found = false;

		// create csvReader object passing file reader as a parameter
		CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
		String[] record;

		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(tmpFD, true)));

		while ((record = csvReader.readNext()) != null) {
			int posId = AlbumAttributesOrder.getIndex_AlbumID();

			boolean recordFound = record[posId].equals(String.valueOf(instance.getAlbumId()));
			if (!recordFound) {
				csvWriter.writeNext(record);
			} else {
				found = recordFound;
			}
		}
		csvWriter.flush();

		csvReader.close();
		csvWriter.close();

		if (found) {
			Files.move(tmpFD.toPath(), fd.toPath(), REPLACE_EXISTING);
		} else {
			tmpFD.delete();
		}
	}

	private static class AlbumAttributesOrder {
		public static int getIndex_AlbumID() {
			return 0;
		}

		public static int getIndex_Titolo() {
			return 1;
		}

		public static int getIndex_Artista() {
			return 2;
		}

		public static int getIndex_Anno() {
			return 3;
		}
	}

	private static void clanup() {
		File fd = new File(CSV_FILE_NAME);
		fd.delete();
	}
	
	public static void main(String[] args) throws Exception {
		clanup();
		
		Album a0 = new Album(0, "artista0", "titolo0", 2020);
		Album a1 = new Album(1, "artista1", "titolo1", 2021);
		Album a2 = new Album(2, "artista2", "titolo2", 2022);
		Album a3 = new Album(3, "artista3", "titolo3", 2023);
		Album a4 = new Album(4, "artista4", "titolo1", 2020);

		AlbumDAOCSV dao = new AlbumDAOCSV();
		dao.saveAlbum(a0);
		dao.saveAlbum(a1);
		dao.saveAlbum(a2);
		dao.saveAlbum(a3);
		dao.saveAlbum(a4);

		dao.removeAlbumById(a2);
		List<Album> albumList;
		try {
			albumList = dao.retreiveByAlbumName("titolo2");			
		} catch (Exception e) {
			albumList = new ArrayList<Album>();
		}
		assertEquals(0, albumList.size());

		boolean failedSaving = false;
		a3.setArtista("artista5");
		a3.setTitolo("titolo5");
		a3.setAnno(2025);
		try {
			dao.saveAlbum(a3);
		} catch (Exception e) {
			failedSaving = true;
		}
		assertTrue(failedSaving);

		albumList = dao.retreiveByAlbumName("titolo0");
		assertEquals(1, albumList.size());

		albumList = dao.retreiveByAlbumName("titolo1");
		assertEquals(2, albumList.size());

		albumList = dao.retreiveByAlbumName("titolo1");
		assertEquals(2, albumList.size());
	}

}
