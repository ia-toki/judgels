import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';

import { ContestContestantAddDialog, ContestContestantAddDialogProps } from './ContestContestantAddDialog';

describe('ContestContestantAddDialog', () => {
  let onUpsertContestants: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpsertContestants = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedContestantProfilesMap: {}, alreadyContestantProfilesMap: {} }));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ContestContestantAddDialogProps = {
      contest,
      onUpsertContestants,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestContestantAddDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('add contestants dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertContestants).toHaveBeenCalledWith(contestJid, ['andi', 'budi', 'caca']);
  });
});
