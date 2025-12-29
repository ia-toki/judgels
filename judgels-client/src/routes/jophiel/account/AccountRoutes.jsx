import { Navigate, Outlet } from 'react-router';

import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ChangeAvatarPage from './changeAvatar/ChangeAvatarPage/ChangeAvatarPage';
import InfoPage from './info/InfoPage/InfoPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

function AccountLayout() {
  const sidebarItems = [
    {
      path: 'info',
      title: 'Info',
    },
    {
      path: 'avatar',
      title: 'Change avatar',
    },
    {
      path: 'password',
      title: 'Reset password',
    },
  ];

  const contentWithSidebarProps = {
    title: 'My account',
    items: sidebarItems,
    smallContent: true,
    basePath: '/account',
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

const AccountLayoutWithBreadcrumb = withBreadcrumb('My account')(AccountLayout);

export const accountRoutes = [
  {
    element: <AccountLayoutWithBreadcrumb />,
    children: [
      {
        index: true,
        element: <Navigate to="info" replace />,
      },
      {
        path: 'info',
        element: <InfoPage />,
      },
      {
        path: 'avatar',
        element: <ChangeAvatarPage />,
      },
      {
        path: 'password',
        element: <ResetPasswordPage />,
      },
    ],
  },
];
