import { Layers, Properties, TimelineEvents } from '@blueprintjs/icons';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { isTLX } from '../../../conf';
import { createDocumentTitle } from '../../../utils/title';
import { selectUserJid, selectUsername } from '../modules/profileSelectors';

import * as profileActions from '../modules/profileActions';

export default function ProfilesLayout() {
  const { username: paramUsername } = useParams({ strict: false });
  const dispatch = useDispatch();
  const userJid = useSelector(selectUserJid);
  const username = useSelector(selectUsername);

  useEffect(() => {
    dispatch(profileActions.getUser(paramUsername));
    document.title = createDocumentTitle(paramUsername);

    return () => {
      dispatch(profileActions.clearUser());
    };
  }, [paramUsername]);

  // Optimization:
  // We wait until we get the username from the backend only if the current username is different from the persisted one.
  if (!userJid || username !== paramUsername) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      path: '',
      titleIcon: <Properties />,
      title: 'Summary',
    },
    {
      path: 'contest-history',
      titleIcon: <TimelineEvents />,
      title: 'Contest history',
    },
    ...(isTLX()
      ? [
          {
            path: 'submission-history',
            titleIcon: <Layers />,
            title: 'Submission history',
          },
        ]
      : []),
  ];

  const contentWithSidebarProps = {
    title: 'Profile Menu',
    items: sidebarItems,
    contentHeader: <h2>Profile of {username}</h2>,
    basePath: `/profiles/${username}`,
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
