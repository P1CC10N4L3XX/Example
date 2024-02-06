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
package it.uniroma2.dicii.ispw.jdbcExamples;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.uniroma2.dicii.ispw.jdbcExamples.dao.AlbumDAO;
import it.uniroma2.dicii.ispw.jdbcExamples.dao.AlbumDAOCSV;
import it.uniroma2.dicii.ispw.jdbcExamples.dao.AlbumDAOJDBC;
import it.uniroma2.dicii.ispw.jdbcExamples.entities.Album;
import it.uniroma2.dicii.ispw.jdbcExamples.exceptions.DuplicatedRecordException;

public class SimpleJDBCExample {

	private AlbumDAO albumDAO = null;
	
	public SimpleJDBCExample(TypesOfPersistenceLayer p) throws IOException {
		switch (p) {
		case JDBC:			
			this.albumDAO = new AlbumDAOJDBC();
			break;
		case FileSystem:
			this.albumDAO = new AlbumDAOCSV();
			break;
		default:
			this.albumDAO = new AlbumDAOCSV();
			break;
		}
	}
	
	public void runTheExample() {
		Album newAlbumInstance = new Album(55, "Pearl Jam", "Ten", 1991);

		try {
			String searchKey = "Made In Japan";
			System.out.println("Looking for albums named: " + searchKey);
			List<Album> list = this.albumDAO.retreiveByAlbumName(searchKey);

			int i=0;
			for (Album album : list) {
				i++;
				System.out.println("Result "+ i + ": " +album.toString());
			}

			try {
				System.out.println("**********************************");
				System.out.println("Adding the new album instance named: " + newAlbumInstance.getTitolo());
				this.albumDAO.saveAlbum(newAlbumInstance);
				System.out.println("... done!");
// Sperimentate l'uso di eccezioni custom. Rimuovete il commento dall'istruzione che segue, ed eseguite il codice in modalità debug.			
				this.albumDAO.saveAlbum(newAlbumInstance);
			} catch (DuplicatedRecordException dre) {
					System.out.println("... ops! Album already present.\n Removing the old item (i.e. ID: "+newAlbumInstance.getAlbumId()+")...");
					this.albumDAO.removeAlbumById(newAlbumInstance);
					System.out.println("... removal completed!");
					Album updatedAlbumInstance = new Album(56, newAlbumInstance.getArtista(), newAlbumInstance.getTitolo(), newAlbumInstance.getAnno());
					System.out.println("Replacing with an updated item (i.e. ID: "+updatedAlbumInstance.getAlbumId()+")...");
					this.albumDAO.saveAlbum(updatedAlbumInstance);
					System.out.println("... replacement completed!");
					newAlbumInstance = updatedAlbumInstance;
			}
			
			System.out.println("**********************************");
			System.out.println("Removing the item just added ...");
			albumDAO.removeAlbumById(newAlbumInstance);
			System.out.println("... done!");
			System.out.println("**********************************");
		} catch (SQLException se) {
			// Errore durante l'apertura della connessione
			se.printStackTrace();
		} catch (ClassNotFoundException driverEx) {
			// Errore nel loading del driver
			driverEx.printStackTrace();
		} catch (Exception e) {
			// Errore nel loading del driver o possibilmente nell'accesso al filesystem 
			e.printStackTrace();
		}		
	}
	
	public static void main(String args[]) throws IOException {
				
// Alternate il tipo dello strato di persistenza di riferimento.
// Notate che l'implementazione dell'applicazione non cambia.
// La responsabilità dell'interfacciamento verso lo strato di persistenza è tutta delegata ai DAO 		
//		SimpleJDBCExample toyExample = new SimpleJDBCExample(TypesOfPersistenceLayer.JDBC);
		SimpleJDBCExample toyExample = new SimpleJDBCExample(TypesOfPersistenceLayer.FileSystem);

		toyExample.runTheExample();
	}
}

enum TypesOfPersistenceLayer{
	JDBC,
	FileSystem
}
