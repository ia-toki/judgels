import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { UserWidget, UserWidgetProps } from './UserWidget';
import { User } from '../../modules/api/jophiel/user';
import { Profile } from '../../modules/api/jophiel/profile';

describe('UserWidget', () => {
  let user: User | undefined;
  let onRenderAvatar = () => Promise.resolve('url');
  let onGetProfile = () => Promise.resolve({ username: 'user' } as Profile);

  let wrapper: ShallowWrapper;

  const render = () => {
    const props: any = {
      user,
      onRenderAvatar,
      onGetProfile,
    } as Partial<UserWidgetProps>;

    wrapper = shallow(<UserWidget {...props} />);
  };

  beforeEach(() => {
    user = undefined;
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
      render();
    });

    // TODO(fushar): fix
    it.skip('shows the user widget', () => {
      expect(wrapper.find('[data-key="username"]').text()).toContain('user');
    });
  });
});
