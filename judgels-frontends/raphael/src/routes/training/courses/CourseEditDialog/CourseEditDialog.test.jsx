import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { Course } from '../../../../modules/api/jerahmeel/course';
import { CourseEditDialog } from './CourseEditDialog';

const course: Course = {
  id: 1,
  jid: 'courseJid',
  slug: 'course',
  name: 'Course',
  description: 'This is a course',
};

describe('CourseEditDialog', () => {
  let onUpdateCourse: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpdateCourse = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      isOpen: true,
      course,
      onCloseDialog: jest.fn(),
      onUpdateCourse,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <CourseEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const slug = wrapper.find('input[name="slug"]');
    slug.simulate('change', { target: { value: 'new-course' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New course' } });

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'New description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateCourse).toHaveBeenCalledWith(course.jid, {
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});
