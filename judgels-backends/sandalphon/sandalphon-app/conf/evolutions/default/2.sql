-- !Ups

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_problem_setter`
--

CREATE TABLE `sandalphon_problem_setter` (
  `id` bigint(20) NOT NULL,
  `problemJid` varchar(32) NOT NULL,
  `role` varchar(20) NOT NULL,
  `userJid` varchar(32) NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL
);

--
-- Indexes for table `sandalphon_problem_setter`
--
ALTER TABLE `sandalphon_problem_setter`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `problemJid_role_userJid` (`problemJid`,`role`,`userJid`) USING BTREE,
  ADD KEY `userJid_role` (`userJid`,`role`) USING BTREE;

--
-- AUTO_INCREMENT for table `sandalphon_problem_setter`
--
ALTER TABLE `sandalphon_problem_setter`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
