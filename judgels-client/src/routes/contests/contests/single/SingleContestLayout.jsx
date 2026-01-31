import { Intent, Tag } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useLocation, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { ContestTab } from '../../../../modules/api/uriel/contestWeb';
import { contestBySlugQueryOptions } from '../../../../modules/queries/contest';
import { contestWebConfigQueryOptions } from '../../../../modules/queries/contestWeb';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { createDocumentTitle } from '../../../../utils/title';
import { EditContest } from '../modules/contestReducer';
import { selectIsEditingContest } from '../modules/contestSelectors';
import ContestAnnouncementsWidget from './components/ContestAnnouncementsWidget/ContestAnnouncementsWidget';
import ContestClarificationsWidget from './components/ContestClarificationsWidget/ContestClarificationsWidget';
import { ContestEditDialog } from './components/ContestEditDialog/ContestEditDialog';
import ContestStateWidget from './components/ContestStateWidget/ContestStateWidget';
import { contestIcon } from './modules/contestIcon';

import './SingleContestLayout.scss';

export default function SingleContestLayout() {
  const { contestSlug } = useParams({ strict: false });
  const { pathname } = useLocation();
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const { data: contestWebConfig } = useSuspenseQuery(contestWebConfigQueryOptions(token, contestSlug));
  const dispatch = useDispatch();
  const isEditingContest = useSelector(selectIsEditingContest);

  const onSetNotEditingContest = () => dispatch(EditContest(false));

  useEffect(() => {
    document.title = createDocumentTitle(contest.name);
  }, [contestSlug, contest.name]);

  const visibleTabs = contestWebConfig && contestWebConfig.visibleTabs;
  const sidebarItems = [
    {
      path: '',
      titleIcon: contestIcon[ContestTab.Overview],
      title: 'Overview',
    },
    {
      path: 'announcements',
      titleIcon: contestIcon[ContestTab.Announcements],
      title: (
        <div className="tab-item-with-widget">
          <div className="float-left">Announcements</div>
          <div className="float-right">
            <ContestAnnouncementsWidget />
          </div>
          <div className="clearfix" />
        </div>
      ),
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
      title: (
        <div className="tab-item-with-widget">
          <div className="float-left">Editorial</div>
          <div className="float-right">
            <Tag className="normal-weight" intent={Intent.WARNING}>
              NEW
            </Tag>
          </div>
          <div className="clearfix" />
        </div>
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
      title: (
        <div className="tab-item-with-widget">
          <div className="float-left">Clarifications</div>
          <div className="float-right">
            <ContestClarificationsWidget />
          </div>
          <div className="clearfix" />
        </div>
      ),
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
    action: <ContestRoleTag role={contestWebConfig.role} />,
    items: sidebarItems,
    basePath: `/contests/${contestSlug}`,
    contentHeader: (
      <div className="single-contest-routes__header">
        <div className="single-contest-routes__heading">
          <h2>{contest.name}</h2>
          <div className="single-contest-routes__action">
            <ContestEditDialog
              contest={contest}
              canaManage={contestWebConfig.canManage}
              isEditingContest={isEditingContest}
              onSetNotEditingContest={onSetNotEditingContest}
            />
          </div>
        </div>
        <ContestStateWidget />
      </div>
    ),
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
