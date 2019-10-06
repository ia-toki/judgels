-- !Ups

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_bundle_grading`
--

CREATE TABLE `sandalphon_bundle_grading` (
  `id` bigint(20) NOT NULL,
  `jid` varchar(32) NOT NULL,
  `submissionJid` varchar(32) NOT NULL,
  `score` int(11) NOT NULL,
  `details` longtext,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_bundle_submission`
--

CREATE TABLE `sandalphon_bundle_submission` (
  `id` bigint(20) NOT NULL,
  `jid` varchar(32) NOT NULL,
  `containerJid` varchar(32) DEFAULT NULL,
  `problemJid` varchar(32) NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_lesson`
--

CREATE TABLE `sandalphon_lesson` (
  `id` bigint(20) NOT NULL,
  `jid` varchar(32) NOT NULL,
  `slug` varchar(100) NOT NULL,
  `additionalNote` text NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_lesson_partner`
--

CREATE TABLE `sandalphon_lesson_partner` (
  `id` bigint(20) NOT NULL,
  `lessonJid` varchar(32) NOT NULL,
  `userJid` varchar(32) NOT NULL,
  `config` text NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_problem`
--

CREATE TABLE `sandalphon_problem` (
  `id` bigint(20) NOT NULL,
  `jid` varchar(32) NOT NULL,
  `slug` varchar(100) NOT NULL,
  `additionalNote` text NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_problem_partner`
--

CREATE TABLE `sandalphon_problem_partner` (
  `id` bigint(20) NOT NULL,
  `problemJid` varchar(32) NOT NULL,
  `userJid` varchar(32) NOT NULL,
  `baseConfig` text NOT NULL,
  `childConfig` text NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_programming_grading`
--

CREATE TABLE `sandalphon_programming_grading` (
  `id` bigint(20) NOT NULL,
  `jid` varchar(32) NOT NULL,
  `submissionJid` varchar(32) NOT NULL,
  `verdictCode` varchar(10) NOT NULL,
  `verdictName` varchar(50) NOT NULL,
  `score` int(11) NOT NULL,
  `details` longtext DEFAULT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `sandalphon_programming_submission`
--

CREATE TABLE `sandalphon_programming_submission` (
  `id` bigint(20) NOT NULL,
  `jid` varchar(32) NOT NULL,
  `containerJid` varchar(32) DEFAULT NULL,
  `problemJid` varchar(32) NOT NULL,
  `gradingEngine` varchar(50) NOT NULL,
  `gradingLanguage` varchar(50) NOT NULL,
  `createdAt` datetime(3) NOT NULL,
  `createdBy` varchar(32) DEFAULT NULL,
  `createdIp` varchar(100) DEFAULT NULL,
  `updatedAt` datetime(3) NOT NULL,
  `updatedBy` varchar(32) DEFAULT NULL,
  `updatedIp` varchar(100) DEFAULT NULL
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `sandalphon_bundle_grading`
--
ALTER TABLE `sandalphon_bundle_grading`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jid` (`jid`),
  ADD KEY `submissionJid` (`submissionJid`);

--
-- Indexes for table `sandalphon_bundle_submission`
--
ALTER TABLE `sandalphon_bundle_submission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jid` (`jid`),
  ADD KEY `createdAt` (`createdAt`),
  ADD KEY `createdBy` (`createdBy`),
  ADD KEY `problemJid_createdBy` (`problemJid`,`createdBy`) USING BTREE,
  ADD KEY `problemJid_createdAt` (`problemJid`,`createdAt`) USING BTREE;

--
-- Indexes for table `sandalphon_lesson`
--
ALTER TABLE `sandalphon_lesson`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jid` (`jid`),
  ADD UNIQUE KEY `slug` (`slug`),
  ADD KEY `createdAt` (`createdAt`),
  ADD KEY `updatedAt` (`updatedAt`),
  ADD KEY `createdBy` (`createdBy`);

--
-- Indexes for table `sandalphon_lesson_partner`
--
ALTER TABLE `sandalphon_lesson_partner`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `lessonJid_userJid` (`lessonJid`,`userJid`) USING BTREE;

--
-- Indexes for table `sandalphon_problem`
--
ALTER TABLE `sandalphon_problem`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jid` (`jid`),
  ADD UNIQUE KEY `slug` (`slug`),
  ADD KEY `createdAt` (`createdAt`),
  ADD KEY `createdBy` (`createdBy`),
  ADD KEY `updatedAt` (`updatedAt`);

--
-- Indexes for table `sandalphon_problem_partner`
--
ALTER TABLE `sandalphon_problem_partner`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `problemJid_userJid` (`problemJid`,`userJid`) USING BTREE;

--
-- Indexes for table `sandalphon_programming_grading`
--
ALTER TABLE `sandalphon_programming_grading`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jid` (`jid`),
  ADD KEY `submissionJid` (`submissionJid`),
  ADD KEY `verdictCode` (`verdictCode`);

--
-- Indexes for table `sandalphon_programming_submission`
--
ALTER TABLE `sandalphon_programming_submission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jid` (`jid`),
  ADD KEY `problemJid_createdBy` (`problemJid`,`createdBy`) USING BTREE,
  ADD KEY `problemJid_gradingLanguage` (`problemJid`,`gradingLanguage`) USING BTREE,
  ADD KEY `problemJid_createdAt` (`problemJid`,`createdAt`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `sandalphon_bundle_grading`
--
ALTER TABLE `sandalphon_bundle_grading`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_bundle_submission`
--
ALTER TABLE `sandalphon_bundle_submission`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_lesson`
--
ALTER TABLE `sandalphon_lesson`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_lesson_partner`
--
ALTER TABLE `sandalphon_lesson_partner`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_problem`
--
ALTER TABLE `sandalphon_problem`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_problem_partner`
--
ALTER TABLE `sandalphon_problem_partner`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_programming_grading`
--
ALTER TABLE `sandalphon_programming_grading`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sandalphon_programming_submission`
--
ALTER TABLE `sandalphon_programming_submission`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
