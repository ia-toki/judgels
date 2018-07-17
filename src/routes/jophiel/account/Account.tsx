import * as React from 'react';

import { FullPageLayout } from '../../../components/layouts/FullPageLayout/FullPageLayout';
import UserRoute from '../../../components/UserRoute/UserRoute';
import ProfileContainer from './routes/profile/Profile/Profile';
import ChangePasswordContainer from './routes/changePassword/ChangePassword/ChangePassword';
import ChangeAvatarContainer from './routes/changeAvatar/ChangeAvatar/ChangeAvatar';
import ContentWithSidebarContainer, {
  ContentWithSidebarContainerItem,
  ContentWithSidebarContainerProps,
} from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

export const Account = () => {
  const sidebarItems: ContentWithSidebarContainerItem[] = [
    {
      id: 'profile',
      title: 'Profile',
      routeComponent: UserRoute,
      component: ProfileContainer,
    },
    {
      id: 'avatar',
      title: 'Change avatar',
      routeComponent: UserRoute,
      component: ChangeAvatarContainer,
    },
    {
      id: 'password',
      title: 'Change password',
      routeComponent: UserRoute,
      component: ChangePasswordContainer,
    },
  ];

  const contentWithSidebarContainerProps: ContentWithSidebarContainerProps = {
    title: 'My account',
    items: sidebarItems,
    smallContent: true,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebarContainer {...contentWithSidebarContainerProps} />
    </FullPageLayout>
  );
};

const AccountContainer = withBreadcrumb('My account')(Account);
export default AccountContainer;
