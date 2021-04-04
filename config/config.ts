import { defineConfig } from 'umi';

export default defineConfig({
  nodeModulesTransform: {
    type: 'none',
  },
  fastRefresh: {},
  metas: [
    {
      name: 'viewport',
      content:
        'width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no',
    },
  ],
  chainWebpack: (config, { webpack }) => {
    config.module
      .rule('media')
      .test(/\.(mp4|mp3|zip)$/)
      .use('url-loader')
      .loader(require.resolve('url-loader'))
      .options({ limit: 1024 });
    // config.merge({
    //   optimization: {
    //     splitChunks: {
    //       chunks: 'all',
    //       minSize: 30000,
    //       minChunks: 3,
    //       automaticNameDelimiter: '.',
    //       cacheGroups: {
    //         vendor: {
    //           name: 'vendors',
    //           test({ resource }: { resource: string }) {
    //             return /[\\/]node_modules[\\/]/.test(resource)
    //           },
    //           priority: 10
    //         }
    //       }
    //     }
    //   }
    // })

    return config;
  },
});
