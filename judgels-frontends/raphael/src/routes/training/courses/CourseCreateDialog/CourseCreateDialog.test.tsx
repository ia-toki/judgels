import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { CourseCreateDialog } from './CourseCreateDialog';

describe('CourseCreateDialog', () => {
  let onGetCourseConfig: jest.Mock<any>;
  let onCreateCourse: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onCreateCourse = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      onGetCourseConfig,
      onCreateCourse,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <CourseCreateDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('create dialog form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const slug = wrapper.find('input[name="slug"]');
    slug.simulate('change', { target: { value: 'new-course' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New course' } });

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'New description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateCourse).toHaveBeenCalledWith({
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});
