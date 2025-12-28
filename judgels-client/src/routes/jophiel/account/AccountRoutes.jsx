import { Navigate, Route, Routes } from 'react-router';

import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import UserRoute from '../../../components/UserRoute/UserRoute';
import ChangeAvatarPage from './changeAvatar/ChangeAvatarPage/ChangeAvatarPage';
import InfoPage from './info/InfoPage/InfoPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

function AccountRoutes() {
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
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <UserRoute>
          <Routes>
            <Route index element={<Navigate to="info" replace />} />
            <Route path="info" element={<InfoPage />} />
            <Route path="avatar" element={<ChangeAvatarPage />} />
            <Route path="password" element={<ResetPasswordPage />} />
          </Routes>
        </UserRoute>
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

export default withBreadcrumb('My account')(AccountRoutes);
