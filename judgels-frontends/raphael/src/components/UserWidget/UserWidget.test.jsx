import { shallow } from 'enzyme';
import * as React from 'react';

import { UserWidget } from './UserWidget';

describe('UserWidget', () => {
  let user;
  let profile;
  let onRenderAvatar = () => Promise.resolve('url');

  let wrapper;

  const render = () => {
    const props = {
      user,
      isWebConfigLoaded: true,
      profile,
      onRenderAvatar,
    };

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
      user = { jid: 'jid123', username: 'user', email: 'user@domain.com' };
      profile = { username: 'user' };
      render();
    });

    it('shows the user widget', () => {
      expect(wrapper.find('[data-key="username"]').text()).toContain('user');
    });
  });
});
