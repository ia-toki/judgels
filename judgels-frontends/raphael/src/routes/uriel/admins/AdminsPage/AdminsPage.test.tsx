import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { Admin, AdminsResponse } from 'modules/api/uriel/admin';

import { createAdminsPage } from './AdminsPage';

describe('AdminsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let adminActions: jest.Mocked<any>;

  const response: AdminsResponse = {
    data: { page: [], totalCount: 0 },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
  };

  const render = () => {
    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const AdminsPage = createAdminsPage(adminActions);

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <AdminsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    adminActions = {
      getAdmins: jest.fn().mockReturnValue(() => Promise.resolve(response)),
    };
  });

  describe('when there are no admins', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no admins', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No admins.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are admins', () => {
    beforeEach(() => {
      const admins: Admin[] = [
        {
          userJid: 'userJid1',
        } as Admin,
        {
          userJid: 'userJid2',
        } as Admin,
      ];
      adminActions.getAdmins.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: admins, totalCount: 2 } })
      );

      render();
    });

    it('shows the admins', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
