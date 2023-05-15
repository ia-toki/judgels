import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Various problem types',
    description: (
      <>
        Batch, interactive, output-only, and functional / grader (IOI 2010+) problem types,
        with and without subtasks. Supports multilanguage problem statement.
      </>
    ),
  },
  {
    title: 'Rich contest features',
    description: (
      <>
        IOI- and ICPC-style contests. Supports virtual contests where contestants can start at different times.
        Various user roles such as contest managers and supervisors.
      </>
    ),
  },
  {
    title: 'Easy to deploy',
    description: (
      <>
        Packaged as Docker images. Can be easily deployed using our Ansible scripts.
      </>
    ),
  },
];

function Feature({title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
