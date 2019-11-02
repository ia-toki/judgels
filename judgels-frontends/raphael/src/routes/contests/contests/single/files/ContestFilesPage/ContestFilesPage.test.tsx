import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { ContestFile, ContestFilesResponse } from '../../../../../../modules/api/uriel/contestFile';

import { createContestFilesPage } from './ContestFilesPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestFilesPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestFileActions: jest.Mocked<any>;

  const response: ContestFilesResponse = {
    data: [],
    config: { canManage: true },
  };

  const render = () => {
    const store: any = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    const ContestFilesPage = createContestFilesPage(contestFileActions);
    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestFilesPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestFileActions = {
      getFiles: jest.fn().mockReturnValue(() => Promise.resolve(response)),
      uploadFile: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };
    render();
  });

  describe('when there are no files', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no files', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No files.');
      expect(wrapper.find('tr')).toHaveLength(1 + 0);
    });
  });

  describe('when there are files', () => {
    beforeEach(() => {
      const files: ContestFile[] = [
        {
          name: 'editorial.pdf',
          size: 100,
          lastModifiedTime: 12345,
        } as ContestFile,
        {
          name: 'solutions.zip',
          size: 100,
          lastModifiedTime: 12345,
        } as ContestFile,
      ];
      contestFileActions.getFiles.mockReturnValue(() => Promise.resolve({ ...response, data: files }));

      render();
    });

    it('shows the files', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(1 + 1 + 2);
    });
  });

  test('upload form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const file = wrapper.find('input[name="file"]');
    file.simulate('change', { target: { files: [{ name: 'editorial.txt', size: 1000 }] } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestFileActions.uploadFile).toHaveBeenCalledWith(contestJid, {
      name: 'editorial.txt',
      size: 1000,
    } as File);
  });
});
