import React from 'react';

declare global {
  interface Window {
    MathJax: any;
  }
}

export class MathjaxWrapper extends React.Component {
  componentDidMount() {
    const publicUrl = process.env.PUBLIC_URL;
    this.insertScript('MathJax-config', publicUrl + '/var/conf/mathjax-config.js');
    this.insertScript('MathJax-script', publicUrl + '/mathjax/tex-svg.js');
  }

  componentDidUpdate() {
    window.MathJax.typeset();
  }

  private insertScript(id: string, url: string) {
    const script = document.createElement('script');

    script.id = id;
    script.src = url;
    script.defer = true;

    document.body.appendChild(script);
  }

  render() {
    return <div className="mathjax-wrapper">{this.props.children}</div>;
  }
}
