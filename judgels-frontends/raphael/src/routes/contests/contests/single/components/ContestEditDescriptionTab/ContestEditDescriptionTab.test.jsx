import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, contestJid } from '../../../../../../fixtures/state';
import ContestEditDescriptionTab from './ContestEditDescriptionTab';
import { contestReducer, PutContest } from '../../../modules/contestReducer';
import * as contestActions from '../../../modules/contestActions';

jest.mock('../../../modules/contestActions');

describe('ContestEditDescriptionTab', () => {
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    (contestActions.getContestDescription as jest.Mock).mockReturnValue(() => Promise.resolve('current description'));

    (contestActions.updateContestDescription as jest.Mock).mockReturnValue(() => Promise.resolve({}));

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
