-- phpMyAdmin SQL Dump
-- version 4.6.6deb5ubuntu0.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 14, 2020 at 03:48 PM
-- Server version: 5.7.32-0ubuntu0.18.04.1
-- PHP Version: 7.2.24-0ubuntu0.18.04.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `MySimpleMusicCatalog-DB`
--
CREATE DATABASE IF NOT EXISTS `MySimpleMusicCatalog-DB` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `MySimpleMusicCatalog-DB`;

-- --------------------------------------------------------

--
-- Table structure for table `Album`
--

CREATE TABLE `Album` (
  `AlbumId` int(11) NOT NULL,
  `Titolo` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `Artista` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `Anno` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `Album`
--

INSERT INTO `Album` (`AlbumId`, `Titolo`, `Artista`, `Anno`) VALUES
(1, 'Made In Japan', 'Deep Purple', 1972),
(2, 'Be', 'Pain Of Salvation', 2004),
(3, 'Images And Words', 'Dream Theater', 1992),
(4, 'The Human Equation', 'Ayreon', 2004);

-- --------------------------------------------------------

--
-- Table structure for table `UsersInTheCatalog`
--

CREATE TABLE `UsersInTheCatalog` (
  `nome` varchar(30) COLLATE utf8_bin NOT NULL,
  `cognome` varchar(30) COLLATE utf8_bin NOT NULL,
  `username` varchar(30) COLLATE utf8_bin NOT NULL,
  `password` varchar(30) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `UsersInTheCatalog`
--

INSERT INTO `UsersInTheCatalog` (`nome`, `cognome`, `username`, `password`) VALUES
('Charlie', 'Brown', 'charlie', 'Snoopy'),
('Linus', 'van Pelt', 'linus', 'thisisfoo');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Album`
--
ALTER TABLE `Album`
  ADD PRIMARY KEY (`AlbumId`);

--
-- Indexes for table `UsersInTheCatalog`
--
ALTER TABLE `UsersInTheCatalog`
  ADD PRIMARY KEY (`username`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
