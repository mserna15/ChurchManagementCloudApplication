-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Apr 02, 2026 at 04:10 AM
-- Server version: 5.7.24
-- PHP Version: 8.3.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `churchcmsdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `members`
--

CREATE TABLE `members` (
  `id` bigint(20) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `join_date` date DEFAULT NULL,
  `ministry_group_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `members`
--

INSERT INTO `members` (`id`, `first_name`, `last_name`, `email`, `phone`, `join_date`, `ministry_group_id`) VALUES
(1, 'James', 'Carter', 'james.carter@email.com', '602-555-0101', '2022-01-15', 1),
(2, 'Sofia', 'Ramirez', 'sofia.ramirez@email.com', '602-555-0102', '2022-03-22', 1),
(3, 'Ethan', 'Brooks', 'ethan.brooks@email.com', '602-555-0103', '2021-08-10', 2),
(4, 'Olivia', 'Turner', 'olivia.turner@email.com', '602-555-0104', '2023-02-01', 2),
(5, 'Liam', 'Foster', 'liam.foster@email.com', '602-555-0105', '2020-11-30', 3),
(6, 'Emma', 'Nguyen', 'emma.nguyen@email.com', '602-555-0106', '2021-05-18', 3),
(7, 'Noah', 'Patel', 'noah.patel@email.com', '602-555-0107', '2022-07-04', 4),
(8, 'Ava', 'Johnson', 'ava.johnson@email.com', '602-555-0108', '2023-09-12', 4),
(9, 'Lucas', 'Kim', 'lucas.kim@email.com', '602-555-0109', '2020-03-25', 5),
(10, 'Mia', 'Scott', 'mia.scott@email.com', '602-555-0110', '2021-12-01', 5),
(11, 'Mason', 'Davis', 'mason.davis@email.com', '602-555-0111', '2023-06-15', NULL),
(12, 'Harper', 'Wilson', 'harper.wilson@email.com', '602-555-0112', '2024-01-20', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `ministry_groups`
--

CREATE TABLE `ministry_groups` (
  `id` bigint(20) NOT NULL,
  `group_name` varchar(150) NOT NULL,
  `description` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ministry_groups`
--

INSERT INTO `ministry_groups` (`id`, `group_name`, `description`) VALUES
(1, 'Worship Team', 'Leads the congregation in musical worship during Sunday services.'),
(2, 'Youth Ministry', 'Engages and mentors teenagers through faith-based activities and events.'),
(3, 'Hospitality Team', 'Welcomes visitors and coordinates church events and gatherings.'),
(4, 'Prayer Warriors', 'Dedicated intercessory prayer group that meets weekly.'),
(5, 'Community Outreach', 'Organizes volunteer efforts and charitable programs in the local community.');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `members`
--
ALTER TABLE `members`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `fk_member_group` (`ministry_group_id`);

--
-- Indexes for table `ministry_groups`
--
ALTER TABLE `ministry_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `group_name` (`group_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `members`
--
ALTER TABLE `members`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `ministry_groups`
--
ALTER TABLE `ministry_groups`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `members`
--
ALTER TABLE `members`
  ADD CONSTRAINT `fk_member_group` FOREIGN KEY (`ministry_group_id`) REFERENCES `ministry_groups` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
