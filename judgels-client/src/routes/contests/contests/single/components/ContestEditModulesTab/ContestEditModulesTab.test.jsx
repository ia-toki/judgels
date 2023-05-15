import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestEditModulesTab from './ContestEditModulesTab';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestModuleActions from '../../modules/contestModuleActions';
import * as contestWebActions from '../../modules/contestWebActions';

jest.mock('../../modules/contestModuleActions');
jest.mock('../../modules/contestWebActions');

describe('ContestEditModulesTab', () => {
  let wrapper;

  beforeEach(() => {
    contestWebActions.getContestByJidWithWebConfig.mockReturnValue(() => Promise.resolve());
    contestModuleActions.getModules.mockReturnValue(() => Promise.resolve(['REGISTRATION', 'CLARIFICATION', 'FILE']));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <ContestEditModulesTab />
      </Provider>
    );
  });

  test('tab', () => {
    wrapper.update();

    expect(wrapper.find('h5').map(h5 => h5.text())).toEqual([
      'Registration',
      'Clarification',
      'File',
      'Clarification time limit',
      'Division',
      'Editorial',
      'Freezable scoreboard',
      'Merged scoreboard',
      'External scoreboard',
      'Virtual contest',
      'Paused',
      'Hidden',
    ]);
  });
});
