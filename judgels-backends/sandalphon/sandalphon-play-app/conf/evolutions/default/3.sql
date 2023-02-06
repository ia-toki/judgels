-- !Ups

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_problem_tag`
--

CREATE TABLE `sandalphon_problem_tag` (
  `id` bigint(20) NOT NULL,
  `problemJid` varchar(32) NOT NULL,
  `tag` varchar(50) NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL
);

--
-- Indexes for table `sandalphon_problem_tag`
--
ALTER TABLE `sandalphon_problem_tag`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `problemJid_tag` (`problemJid`,`tag`) USING BTREE,
  ADD KEY `tag` (`tag`) USING BTREE;

--
-- AUTO_INCREMENT for table `sandalphon_problem_tag`
--
ALTER TABLE `sandalphon_problem_tag`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
