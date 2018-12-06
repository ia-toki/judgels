import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from 'fixtures/state';

import { ContestManagerAddDialog, ContestManagerAddDialogProps } from './ContestManagerAddDialog';

describe('AdminAddDialog', () => {
  let onUpsertManagers: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpsertManagers = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props: ContestManagerAddDialogProps = {
      contest,
      onUpsertManagers,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestManagerAddDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('add managers dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertManagers).toHaveBeenCalledWith(contestJid, ['andi', 'budi', 'caca']);
  });
});
