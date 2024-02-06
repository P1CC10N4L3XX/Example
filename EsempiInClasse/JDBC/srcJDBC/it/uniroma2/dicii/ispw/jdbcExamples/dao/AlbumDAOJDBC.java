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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.dicii.ispw.jdbcExamples.dao.queries.CRUDQueries;
import it.uniroma2.dicii.ispw.jdbcExamples.dao.queries.SimpleQueries;
import it.uniroma2.dicii.ispw.jdbcExamples.entities.Album;
import it.uniroma2.dicii.ispw.jdbcExamples.exceptions.DuplicatedRecordException;

public class AlbumDAOJDBC implements AlbumDAO{

//Note this is the USER on the DBMS that has proper privileges in order to access the specific DB 	
	private static String USER = "userSimpleMusicCatalog";
    private static String PASS = "thisisfoo";
//    private static String DB_URL = "jdbc:mysql://localhost:3306/MySimpleMusicCatalog-DB";
//    private static String DB_URL = "jdbc:mysql://saks-db.iasi.cnr.it:3306/MySimpleMusicCatalog-DB";
    private static String DB_URL = "jdbc:mysql://saks-db.iasi.cnr.it:3306/MySimpleMusicCatalog-DB?useSSL=false";
//    private static String DB_URL = "jdbc:postgresql://localhost/MySimpleMusicCatalog-DB";
    private static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
//    private static String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    public List<Album> retreiveByAlbumName(String albumName) throws Exception {
        // STEP 1: dichiarazioni
        Statement stmt = null;
        Connection conn = null;
        List<Album> listOfAlbums = new ArrayList<Album>();
        
        try {
            // STEP 2: loading dinamico del driver mysql
            Class.forName(DRIVER_CLASS_NAME);

            // STEP 3: apertura connessione
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4: creazione ed esecuzione della query
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            // In pratica i risultati delle query possono essere visti come un Array Associativo o un Map
            ResultSet rs = SimpleQueries.selectAlbumByName(stmt, albumName);

            if (!rs.first()){ // rs empty
            	Exception e = new Exception("No Album Found matching with name: "+albumName);
            	throw e;
            }
            
            // riposizionamento del cursore
            rs.first();
            do{
                // lettura delle colonne "by name"
                String artist = rs.getString("Artista");
                int albumId = rs.getInt("AlbumId");
                int year = rs.getInt("Anno");
                
                Album a = new Album(albumId, artist, albumName, year);
                
                listOfAlbums.add(a);

            }while(rs.next());
            
            // STEP 5.1: Clean-up dell'ambiente
            rs.close();
        } finally {
            // STEP 5.2: Clean-up dell'ambiente
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return listOfAlbums;
    }

    public void saveAlbum(Album instance) throws Exception {
        // STEP 1: dichiarazioni
        Statement stmt = null;
        Connection conn = null;
        
        try {
            // STEP 2: loading dinamico del driver mysql
            Class.forName(DRIVER_CLASS_NAME);

            // STEP 3: apertura connessione
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4.1: creazione ed esecuzione della query
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // In pratica i risultati delle query possono essere visti come un Array Associativo o un Map
            ResultSet rs = SimpleQueries.selectAlbumIds(stmt);
            while (rs.next()) {
                // lettura delle colonne "by name"
                int albumId = rs.getInt("AlbumId");
                System.out.println("Found AlbumId: "+ albumId);
                if (albumId == instance.getAlbumId()){
                	DuplicatedRecordException e = new DuplicatedRecordException("Duplicated Instance ID. Id "+albumId + " was already assigned");
                	throw e;                	
                }
            }
            
            rs.close();
            stmt.close();

            // STEP 4.2: creazione ed esecuzione della query
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int result = CRUDQueries.inserisciAlbum(stmt, instance);
            
            // STEP 5.1: Clean-up dell'ambiente
            rs.close();
        } finally {
            // STEP 5.2: Clean-up dell'ambiente        	
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
        }
    }

	public void removeAlbumById(Album instance) throws Exception {
        // STEP 1: dichiarazioni
        Statement stmt = null;
        Connection conn = null;
        
        try {
            // STEP 2: loading dinamico del driver mysql
            Class.forName(DRIVER_CLASS_NAME);

            // STEP 3: apertura connessione
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4.1: creazione ed esecuzione della query
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int result = CRUDQueries.eliminaAlbum(stmt, instance);
            
        } finally {
            // STEP 5.2: Clean-up dell'ambiente        	
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
        }
        
	}
    
}
