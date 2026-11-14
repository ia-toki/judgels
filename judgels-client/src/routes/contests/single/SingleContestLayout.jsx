import { Intent, Tag } from '@blueprintjs/core';
import { Cog } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { ContestRoleTag } from '../../../components/ContestRoleTag/ContestRoleTag';
import { FullWidthPageLayout } from '../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { ContestTab } from '../../../modules/api/contestWeb';
import { contestBySlugQueryOptions } from '../../../modules/queries/contest';
import { contestWebConfigQueryOptions } from '../../../modules/queries/contestWeb';
import { createDocumentTitle } from '../../../utils/title';
import ContestAnnouncementsWidget from './components/ContestAnnouncementsWidget/ContestAnnouncementsWidget';
import ContestClarificationsWidget from './components/ContestClarificationsWidget/ContestClarificationsWidget';
import ContestStateWidget from './components/ContestStateWidget/ContestStateWidget';
import { contestIcon } from './modules/contestIcon';

import './SingleContestLayout.scss';

export default function SingleContestLayout() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { data: contestWebConfig } = useSuspenseQuery(contestWebConfigQueryOptions(contest.jid));

  useEffect(() => {
    document.title = createDocumentTitle(contest.name);
  }, [contestSlug, contest.name]);

  const visibleTabs = contestWebConfig && contestWebConfig.visibleTabs;
  const sidebarItems = [
    {
      path: 'settings',
      titleIcon: <Cog />,
      title: 'Settings',
      trailing: <Tag className="normal-weight">NEW</Tag>,
      disabled: !contestWebConfig.canManage,
    },
    {
      path: '',
      titleIcon: contestIcon[ContestTab.Overview],
      title: 'Overview',
    },
    {
      path: 'announcements',
      titleIcon: contestIcon[ContestTab.Announcements],
      title: 'Announcements',
      trailing: <ContestAnnouncementsWidget />,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Announcements) === -1,
    },
    {
      path: 'problems',
      titleIcon: contestIcon[ContestTab.Problems],
      title: 'Problems',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Problems) === -1,
    },
    {
      path: 'editorial',
      titleIcon: contestIcon[ContestTab.Editorial],
      title: 'Editorial',
      trailing: (
        <Tag className="normal-weight" intent={Intent.WARNING}>
          NEW
        </Tag>
      ),
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Editorial) === -1,
    },
    {
      path: 'contestants',
      titleIcon: contestIcon[ContestTab.Contestants],
      title: 'Contestants',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Contestants) === -1,
    },
    {
      path: 'supervisors',
      titleIcon: contestIcon[ContestTab.Supervisors],
      title: 'Supervisors',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Supervisors) === -1,
    },
    {
      path: 'managers',
      titleIcon: contestIcon[ContestTab.Managers],
      title: 'Managers',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Managers) === -1,
    },
    {
      path: 'submissions',
      titleIcon: contestIcon[ContestTab.Submissions],
      title: 'Submissions',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Submissions) === -1,
    },
    {
      path: 'clarifications',
      titleIcon: contestIcon[ContestTab.Clarifications],
      title: 'Clarifications',
      trailing: <ContestClarificationsWidget />,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Clarifications) === -1,
    },
    {
      path: 'scoreboard',
      titleIcon: contestIcon[ContestTab.Scoreboard],
      title: 'Scoreboard',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Scoreboard) === -1,
    },
    {
      path: 'files',
      titleIcon: contestIcon[ContestTab.Files],
      title: 'Files',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Files) === -1,
    },
    {
      path: 'logs',
      titleIcon: contestIcon[ContestTab.Logs],
      title: 'Logs',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Logs) === -1,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Contest Menu',
    items: sidebarItems,
    basePath: `/contests/${contestSlug}`,
    contentHeader: (
      <Flex className="single-contest-layout__header" alignItems="center" gap={2}>
        <h2>{contest.name}</h2>
        <ContestRoleTag role={contestWebConfig.role} />
      </Flex>
    ),
    contentBanner: <ContestStateWidget />,
  };

  return (
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
