import { Layers, Properties, TimelineEvents } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { isTLX } from '../../../conf';
import { userJidByUsernameQueryOptions } from '../../../modules/queries/profile';
import { createDocumentTitle } from '../../../utils/title';

export default function ProfilesLayout() {
  const { username } = useParams({ strict: false });
  const { data: userJid } = useSuspenseQuery(userJidByUsernameQueryOptions(username));

  useEffect(() => {
    document.title = createDocumentTitle(username);
  }, [username]);

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
