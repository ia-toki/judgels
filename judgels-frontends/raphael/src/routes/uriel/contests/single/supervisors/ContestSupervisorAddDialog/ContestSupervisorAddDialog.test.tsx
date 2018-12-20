import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from 'fixtures/state';

import { ContestSupervisorAddDialog, ContestSupervisorAddDialogProps } from './ContestSupervisorAddDialog';
import { ContestSupervisorUpsertData } from 'modules/api/uriel/contestSupervisor';

describe('ContestSupervisorAddDialog', () => {
  let onUpsertSupervisors: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpsertSupervisors = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props: ContestSupervisorAddDialogProps = {
      contest,
      onUpsertSupervisors: onUpsertSupervisors,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestSupervisorAddDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('add supervisors dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertSupervisors).toHaveBeenCalledWith(contestJid, {
      managementPermissions: [],
      usernames: ['andi', 'budi', 'caca'],
    } as ContestSupervisorUpsertData);
  });
});
