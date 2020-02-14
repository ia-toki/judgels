-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jan 18, 2020 at 03:47 PM
-- Server version: 8.0.12
-- PHP Version: 7.1.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `judgels_jerahmeel`
--

--
-- Dumping data for table `jerahmeel_archive`
--

INSERT INTO `jerahmeel_archive` (`jid`, `slug`, `parentJid`, `name`, `description`, `category`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDARCHElTynLFh3mPVc9d7oWFx', 'ioi', 'JIRARCHoEi3TLoO8jHt0HLMded3', 'IOI', '<p>International Olympiad in Informatics</p>', '1. International', '2019-11-17 15:55:10.000', NULL, '2019-11-17 15:55:10.000', NULL),
('JIDARCHxmGjLvIwXXdqFhngdOrG', 'icpc', 'JIDARCHoEi3TLoO8jHt0HLMded3', 'ICPC', '<p>International Collegiate Programming Contest</p>', '2. Regional', '2019-11-17 15:55:10.000', NULL, '2019-11-17 15:55:10.000', NULL);

--
-- Dumping data for table `jerahmeel_bundle_item_submission`
--

INSERT INTO `jerahmeel_bundle_item_submission` (`jid`, `containerJid`, `problemJid`, `itemJid`, `answer`, `verdict`, `score`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSUBBjr6rIH2fk4WaODaWCZAR', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEM985TWzImiwQqpFmXQsYQ', 'c', 'WRONG_ANSWER', 0, '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 14:48:17.248', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBClkHtTsGXinIa4Oi2HwZ', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEMIXQK2X9taO1LGBDVjR7N', 'c', 'ACCEPTED', 1, '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBoZmJhRwp4wqBVUYXrrDX', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEM985TWzImiwQqpFmXQsYQ', 'a', 'ACCEPTED', 1, '2020-01-18 22:06:34.537', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:34.537', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBjOIrJy20baoiq8LtBcUJ', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEMIXQK2X9taO1LGBDVjR7N', 'c', 'ACCEPTED', 1, '2020-01-18 22:06:36.494', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:36.494', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBk6IvVwfmVEB4Cg971RFe', 'JIDSESSx2sksHzyQNHvMn0yBzLr', 'JIDBUNDKkmglB5WGpSyTTFGScVk', 'JIDITEM9lipxqeitzP6ujFyp3YE', '2', 'ACCEPTED', 1, '2020-01-18 22:29:56.651', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:29:56.651', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_bundle_submission`
--

INSERT INTO `jerahmeel_bundle_submission` (`jid`, `containerJid`, `problemJid`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSUBMiaG02OaXHle61qVwPiaJ', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', '2019-12-23 12:57:00.420', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:57:00.420', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMyPVOA50XhAtsjYgvJ44u', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', '2019-12-23 12:58:00.060', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:58:00.060', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMntVc1WVBgzoBQAtjGoEc', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_chapter`
--

INSERT INTO `jerahmeel_chapter` (`jid`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSESSjboijeXaXam4wM7zwIkA', 'Input/Output', '<p>This is input/output</p>\r\n', '2019-10-19 18:52:44.931', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-19 18:52:44.931', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSpB4nAdte4tUwgSiihTFq', 'Branching', '', '2020-01-18 17:02:07.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 17:02:07.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSx2sksHzyQNHvMn0yBzLr', 'Review', '', '2020-01-18 17:02:53.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 17:02:53.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSnDc5JXrqXDUdPgqhrZXQ', 'Pre-Quiz', '', '2020-01-18 17:04:05.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 17:04:05.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_chapter_lesson`
--

INSERT INTO `jerahmeel_chapter_lesson` (`chapterJid`, `lessonJid`, `alias`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDLESSBQMkZlaJlfafbwDLHoP1', '1', 'VISIBLE', '2019-10-20 20:50:02.112', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-20 20:50:02.112', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_chapter_problem`
--

INSERT INTO `jerahmeel_chapter_problem` (`chapterJid`, `problemJid`, `type`, `alias`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'PROGRAMMING', 'A', 'VISIBLE', '2019-10-24 22:17:26.071', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-24 22:17:26.071', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'BUNDLE', 'X', 'VISIBLE', '2019-10-29 07:37:25.180', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-29 07:37:25.180', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'PROGRAMMING', 'B', 'VISIBLE', '2019-10-29 07:37:25.180', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-29 07:37:25.180', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSpB4nAdte4tUwgSiihTFq', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'PROGRAMMING', 'A', 'VISIBLE', '2019-10-24 22:17:26.071', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-24 22:17:26.071', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDSESSx2sksHzyQNHvMn0yBzLr', 'JIDBUNDKkmglB5WGpSyTTFGScVk', 'BUNDLE', 'X', 'VISIBLE', '2019-10-29 07:37:25.180', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-29 07:37:25.180', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_course`
--

INSERT INTO `jerahmeel_course` (`jid`, `slug`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOURElTynLFh3mPVc9d7oWFx', 'basic', 'Basic Programming', '<p>Lorem ipsum dolor sit amet</p>', '2019-10-19 14:07:15.000', NULL, '2019-10-19 14:07:15.000', NULL),
('JIDCOURa19cx8dp4uVJabPcPsWN', 'competitive', 'Basic Competitive Programming', '', '2019-10-19 15:24:02.000', NULL, '2019-10-19 15:24:02.000', NULL);

--
-- Dumping data for table `jerahmeel_course_chapter`
--

INSERT INTO `jerahmeel_course_chapter` (`courseJid`, `chapterJid`, `alias`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOURElTynLFh3mPVc9d7oWFx', 'JIDSESSjboijeXaXam4wM7zwIkA', 'A', '2019-10-19 18:53:03.789', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-19 18:53:03.789', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCOURElTynLFh3mPVc9d7oWFx', 'JIDSESSpB4nAdte4tUwgSiihTFq', 'B', '2019-10-19 18:53:03.789', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-19 18:53:03.789', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCOURElTynLFh3mPVc9d7oWFx', 'JIDSESSx2sksHzyQNHvMn0yBzLr', 'C', '2019-10-19 18:53:03.789', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-10-19 18:53:03.789', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_curriculum`
--

INSERT INTO `jerahmeel_curriculum` (`jid`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCURR6UjDpTowryGRWXvEJlap', 'Main', 'Report if you find any mistake!', '2019-11-02 19:32:36.391', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-11-02 19:32:36.391', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_problem_set`
--

INSERT INTO `jerahmeel_problem_set` (`jid`, `slug`, `archiveJid`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDPRSEjboijeXaXam4wM7zwIkA', 'ioi-2020', 'JIDARCHElTynLFh3mPVc9d7oWFx', 'IOI 2020', '<p>Indonesia</p>', '2019-11-15 23:15:01.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-11-15 23:15:01.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPRSEqfVvs19a3IaOwWYE9M35', 'ioi-2021', 'JIDARCHElTynLFh3mPVc9d7oWFx', 'IOI 2021', '', '2019-11-15 23:15:01.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-11-15 23:15:01.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPRSEWW7fhFFtagpfHEodXRJw', 'icpc-jakarta', 'JIDARCHxmGjLvIwXXdqFhngdOrG', 'ICPC Jakarta', '<p>Jakarta</p>', '2019-11-15 23:15:01.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-11-15 23:15:01.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_problem_set_problem`
--

INSERT INTO `jerahmeel_problem_set_problem` (`problemSetJid`, `problemJid`, `type`, `alias`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'PROGRAMMING', 'A', 'VISIBLE', '2019-11-17 15:56:58.616', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-11-17 15:56:58.616', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'BUNDLE', 'B', 'VISIBLE', '2019-11-27 20:55:12.768', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-11-27 20:55:12.768', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'PROGRAMMING', 'C', 'VISIBLE', '2019-12-14 10:58:01.522', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-12-14 10:58:01.522', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPRSEqfVvs19a3IaOwWYE9M35', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'PROGRAMMING', 'A', 'VISIBLE', '2019-12-14 10:58:01.522', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2019-12-14 10:58:01.522', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `jerahmeel_programming_grading`
--

INSERT INTO `jerahmeel_programming_grading` (`jid`, `submissionJid`, `verdictCode`, `verdictName`, `score`, `details`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDGRAD7XfQiYn5JGLKGsfsTsnH', 'JIDSUBMV2em0Eo91zaYkmuA2mMw', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[]},{\"id\":-1,\"testCaseResults\":[]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:04:42.650', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:04:46.582', NULL),
('JIDGRADdOPhgipWlnCRMtyW7H9Y', 'JIDSUBMCQDtcyVEKvsM6XYO2Y3q', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":11},\"subtaskIds\":[0]}]},{\"id\":-1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":5},\"subtaskIds\":[-1]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[-1]}]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:05:02.056', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:05:02.957', NULL),
('JIDGRADhMOmFl1FfkSkkcF5I9uO', 'JIDSUBMFIBGlEGTQxpeZXtLoeTD', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"hello.cpp: In function \'int main()\':\\nhello.cpp:6:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%s\\\", s);\\n  ~~~~~^~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":324,\"wallTime\":11},\"subtaskIds\":[0,2]}]},{\"id\":1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":6},\"subtaskIds\":[1,2]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[1,2]}]},{\"id\":2,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[2]}]}],\"subtaskResults\":[{\"id\":1,\"verdict\":{\"code\":\"AC\"},\"score\":30.0},{\"id\":2,\"verdict\":{\"code\":\"AC\"},\"score\":70.0}]}', '2020-01-18 22:06:07.701', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:10.436', NULL),
('JIDGRADbDiq8ytMIRJfXeU3cBTI', 'JIDSUBM4KDniVOMVzMQJEMXNlqd', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":280,\"wallTime\":11},\"subtaskIds\":[0]}]},{\"id\":-1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":3,\"memory\":320,\"wallTime\":3},\"subtaskIds\":[-1]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[-1]}]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:07:54.555', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:07:56.968', NULL),
('JIDGRAD0fZVnolN9V4j5fkOytDE', 'JIDSUBMsj2mVKUY2UqLiIMNEl7U', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":11},\"subtaskIds\":[0]}]},{\"id\":-1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":336,\"wallTime\":4},\"subtaskIds\":[-1]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":2},\"subtaskIds\":[-1]}]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:31:22.997', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:26.718', NULL),
('JIDGRADXj7p2LCEtmR5q7f7aptn', 'JIDSUBMaUyN8hIBErqKCenJUFHe', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[]},{\"id\":-1,\"testCaseResults\":[]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:31:52.410', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:53.579', NULL),
('JIDGRADyUgmy2sfVKBDOQv5tJEv', 'JIDSUBM5fPqSOLoRHsssdgMI93a', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[]},{\"id\":-1,\"testCaseResults\":[]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:32:27.443', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:32:30.756', NULL),
('JIDGRADMZyJb1TiAR0PuRQs1bjC', 'JIDSUBMxm6eXnTKxDj046l6yD7Z', 'WA', '', 30, '{\"compilationOutputs\":{\"source\":\"hello.cpp: In function \'int main()\':\\nhello.cpp:7:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%s\\\", s);\\n  ~~~~~^~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"WA\"},\"score\":\"X\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":324,\"wallTime\":10},\"subtaskIds\":[0,2]}]},{\"id\":1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":5},\"subtaskIds\":[1,2]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":2},\"subtaskIds\":[1,2]}]},{\"id\":2,\"testCaseResults\":[{\"verdict\":{\"code\":\"SKP\"},\"score\":\"?\",\"executionResult\":null,\"subtaskIds\":[2]}]}],\"subtaskResults\":[{\"id\":1,\"verdict\":{\"code\":\"AC\"},\"score\":30.0},{\"id\":2,\"verdict\":{\"code\":\"WA\"},\"score\":0.0}]}', '2020-01-18 22:33:20.351', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:33:22.349', NULL),
('JIDGRADgfq3fKHiz6ImhdzX9TYD', 'JIDSUBMtXWk5Sh2Vuw2KyoPVaeG', 'WA', '', 0, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"WA\"},\"score\":\"X\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":324,\"wallTime\":10},\"subtaskIds\":[0,2]}]},{\"id\":1,\"testCaseResults\":[{\"verdict\":{\"code\":\"WA\"},\"score\":\"X\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":336,\"wallTime\":7},\"subtaskIds\":[1,2]},{\"verdict\":{\"code\":\"SKP\"},\"score\":\"?\",\"executionResult\":null,\"subtaskIds\":[1,2]}]},{\"id\":2,\"testCaseResults\":[{\"verdict\":{\"code\":\"SKP\"},\"score\":\"?\",\"executionResult\":null,\"subtaskIds\":[2]}]}],\"subtaskResults\":[{\"id\":1,\"verdict\":{\"code\":\"WA\"},\"score\":0.0},{\"id\":2,\"verdict\":{\"code\":\"WA\"},\"score\":0.0}]}', '2020-01-18 22:35:33.987', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', '2020-01-18 22:35:36.123', NULL);

--
-- Dumping data for table `jerahmeel_programming_submission`
--

INSERT INTO `jerahmeel_programming_submission` (`jid`, `containerJid`, `problemJid`, `gradingEngine`, `gradingLanguage`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSUBMV2em0Eo91zaYkmuA2mMw', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'Batch', 'Cpp11', '2020-01-18 22:04:42.648', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:04:42.648', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMCQDtcyVEKvsM6XYO2Y3q', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'Batch', 'Cpp11', '2020-01-18 22:05:02.054', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:05:02.054', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMFIBGlEGTQxpeZXtLoeTD', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'BatchWithSubtasks', 'Cpp11', '2020-01-18 22:06:07.700', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:07.700', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBM4KDniVOMVzMQJEMXNlqd', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'Batch', 'Cpp11', '2020-01-18 22:07:54.554', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:07:54.554', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMsj2mVKUY2UqLiIMNEl7U', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'Batch', 'Cpp11', '2020-01-18 22:31:22.996', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:22.996', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDSUBMaUyN8hIBErqKCenJUFHe', 'JIDSESSpB4nAdte4tUwgSiihTFq', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'Batch', 'Cpp11', '2020-01-18 22:31:52.408', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:52.408', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDSUBM5fPqSOLoRHsssdgMI93a', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'Batch', 'Cpp11', '2020-01-18 22:32:27.441', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:32:27.441', 'JIDUSERVfszUmquMh0Ae2laygDp'),
('JIDSUBMxm6eXnTKxDj046l6yD7Z', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'BatchWithSubtasks', 'Cpp11', '2020-01-18 22:33:20.349', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:33:20.349', 'JIDUSERVfszUmquMh0Ae2laygDp'),
('JIDSUBMtXWk5Sh2Vuw2KyoPVaeG', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'BatchWithSubtasks', 'Cpp11', '2020-01-18 22:35:33.986', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', '2020-01-18 22:35:33.986', 'JIDUSER5QiWnVJWzKdjtWtgEfN3');

--
-- Dumping data for table `jerahmeel_stats_user`
--

INSERT INTO `jerahmeel_stats_user` (`userJid`, `score`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 300, '2020-01-18 22:25:46.051', NULL, '2020-01-18 22:25:46.108', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 200, '2020-01-18 22:31:26.731', NULL, '2020-01-18 22:31:53.594', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 130, '2020-01-18 22:32:30.769', NULL, '2020-01-18 22:33:22.358', NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 0, '2020-01-18 22:35:36.131', NULL, '2020-01-18 22:35:36.131', NULL);

--
-- Dumping data for table `jerahmeel_stats_user_chapter`
--

INSERT INTO `jerahmeel_stats_user_chapter` (`userJid`, `chapterJid`, `progress`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDSESSpB4nAdte4tUwgSiihTFq', 1, '2020-01-18 22:25:46.021', NULL, '2020-01-18 22:25:46.021', NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDSESSjboijeXaXam4wM7zwIkA', 2, '2020-01-18 22:25:46.060', NULL, '2020-01-18 22:25:46.079', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDSESSjboijeXaXam4wM7zwIkA', 1, '2020-01-18 22:31:26.722', NULL, '2020-01-18 22:31:26.722', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDSESSpB4nAdte4tUwgSiihTFq', 1, '2020-01-18 22:31:53.584', NULL, '2020-01-18 22:31:53.584', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDSESSpB4nAdte4tUwgSiihTFq', 1, '2020-01-18 22:32:30.761', NULL, '2020-01-18 22:32:30.761', NULL);

--
-- Dumping data for table `jerahmeel_stats_user_course`
--

INSERT INTO `jerahmeel_stats_user_course` (`userJid`, `courseJid`, `progress`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDCOURElTynLFh3mPVc9d7oWFx', 2, '2020-01-18 22:25:46.037', NULL, '2020-01-18 22:25:46.084', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDCOURElTynLFh3mPVc9d7oWFx', 1, '2020-01-18 22:31:53.589', NULL, '2020-01-18 22:31:53.589', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDCOURElTynLFh3mPVc9d7oWFx', 1, '2020-01-18 22:32:30.764', NULL, '2020-01-18 22:32:30.764', NULL);

--
-- Dumping data for table `jerahmeel_stats_user_problem`
--

INSERT INTO `jerahmeel_stats_user_problem` (`userJid`, `problemJid`, `submissionJid`, `verdict`, `score`, `time`, `memory`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'JIDSUBMV2em0Eo91zaYkmuA2mMw', 'AC', 100, 0, 0, '2020-01-18 22:25:46.003', NULL, '2020-01-18 22:25:46.003', NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'JIDSUBM4KDniVOMVzMQJEMXNlqd', 'AC', 100, 3, 356, '2020-01-18 22:25:46.054', NULL, '2020-01-18 22:25:46.098', NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'JIDSUBMFIBGlEGTQxpeZXtLoeTD', 'AC', 100, 2, 356, '2020-01-18 22:25:46.075', NULL, '2020-01-18 22:25:46.075', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'JIDSUBMsj2mVKUY2UqLiIMNEl7U', 'AC', 100, 2, 356, '2020-01-18 22:31:26.719', NULL, '2020-01-18 22:31:26.719', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'JIDSUBMaUyN8hIBErqKCenJUFHe', 'AC', 100, 0, 0, '2020-01-18 22:31:53.581', NULL, '2020-01-18 22:31:53.581', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'JIDSUBM5fPqSOLoRHsssdgMI93a', 'AC', 100, 0, 0, '2020-01-18 22:32:30.758', NULL, '2020-01-18 22:32:30.758', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'JIDSUBMxm6eXnTKxDj046l6yD7Z', 'WA', 30, 0, 0, '2020-01-18 22:33:22.351', NULL, '2020-01-18 22:33:22.351', NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'JIDSUBMtXWk5Sh2Vuw2KyoPVaeG', 'WA', 0, 0, 0, '2020-01-18 22:35:36.124', NULL, '2020-01-18 22:35:36.124', NULL);

--
-- Dumping data for table `jerahmeel_stats_user_problem_set`
--

INSERT INTO `jerahmeel_stats_user_problem_set` (`userJid`, `problemSetJid`, `score`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 100, '2020-01-18 22:25:46.046', NULL, '2020-01-18 22:25:46.046', NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPRSEjboijeXaXam4wM7zwIkA', 200, '2020-01-18 22:25:46.068', NULL, '2020-01-18 22:25:46.103', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDPRSEjboijeXaXam4wM7zwIkA', 100, '2020-01-18 22:31:26.728', NULL, '2020-01-18 22:31:26.728', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 100, '2020-01-18 22:31:53.591', NULL, '2020-01-18 22:31:53.591', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 100, '2020-01-18 22:32:30.766', NULL, '2020-01-18 22:32:30.766', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDPRSEjboijeXaXam4wM7zwIkA', 30, '2020-01-18 22:33:22.355', NULL, '2020-01-18 22:33:22.355', NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'JIDPRSEjboijeXaXam4wM7zwIkA', 0, '2020-01-18 22:35:36.129', NULL, '2020-01-18 22:35:36.129', NULL);
COMMIT;
