import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { UserWidget, UserWidgetProps } from './UserWidget';
import { User } from '../../modules/api/jophiel/user';

describe('UserWidget', () => {
  let user: User | undefined;

  let wrapper: ShallowWrapper;

  const render = () => {
    const props: UserWidgetProps = {
      user,
    };

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

    it('shows the user widget', () => {
      expect(wrapper.find('[data-key="username"]').text()).toContain('user');
    });
  });
});
