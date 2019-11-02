import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { AdminAddDialog, AdminAddDialogProps } from './AdminAddDialog';

describe('AdminAddDialog', () => {
  let onUpsertAdmins: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpsertAdmins = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedAdminProfilesMap: {}, alreadyAdminProfilesMap: {} }));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: AdminAddDialogProps = {
      onUpsertAdmins,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <AdminAddDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('add admins dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertAdmins).toHaveBeenCalledWith(['andi', 'budi', 'caca']);
  });
});
