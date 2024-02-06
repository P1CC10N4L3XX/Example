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

import java.util.List;

import it.uniroma2.dicii.ispw.jdbcExamples.entities.Album;

public interface AlbumDAO {

	List<Album> retreiveByAlbumName(String albumName) throws Exception;
	void saveAlbum(Album instance) throws Exception;
	void removeAlbumById(Album instance) throws Exception;
}
