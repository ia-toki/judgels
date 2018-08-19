import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { User } from 'modules/api/jophiel/user';
import { Profile } from 'modules/api/jophiel/profile';

import { UserWidget, UserWidgetProps } from './UserWidget';

describe('UserWidget', () => {
  let user: User | undefined;
  let profile: Profile | undefined;
  let onRenderAvatar = () => Promise.resolve('url');

  let wrapper: ShallowWrapper;

  const render = () => {
    const props: any = {
      user,
      isWebConfigLoaded: true,
      profile,
      onRenderAvatar,
    } as Partial<UserWidgetProps>;

    wrapper = shallow(<UserWidget {...props} />);
  };

  beforeEach(() => {
    user = undefined;
    profile = undefined;
  });

  describe('when the user is not logged in', () => {
    beforeEach(() => {
      render();
    });

    it('shows guest links', () => {
      expect(wrapper.find('[data-key="login"]')).toHaveLength(1);
      expect(wrapper.find('[data-key="register"]')).toHaveLength(1);
    });
  });

  describe('when the user is logged in', () => {
    beforeEach(() => {
      user = { jid: 'jid123', username: 'user' };
      profile = { username: 'user' } as Profile;
      render();
    });

    // TODO(fushar): fix
    it.skip('shows the user widget', () => {
      expect(wrapper.find('[data-key="username"]').text()).toContain('user');
    });
  });
});
