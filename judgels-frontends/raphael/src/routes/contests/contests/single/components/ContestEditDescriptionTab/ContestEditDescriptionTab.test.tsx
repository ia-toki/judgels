import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, contestJid } from '../../../../../../fixtures/state';

import { createContestEditDescriptionTab } from './ContestEditDescriptionTab';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestEditDescriptionTab', () => {
  let contestActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    contestActions = {
      getContestDescription: jest.fn().mockReturnValue(() => Promise.resolve('current description')),
      updateContestDescription: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };
    const ContestEditDescriptionTab = createContestEditDescriptionTab(contestActions);

    const store: any = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestEditDescriptionTab />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  });

  test('contest edit description tab form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'new description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestActions.updateContestDescription).toHaveBeenCalledWith(contestJid, 'new description');
  });
});
