import { fileURLToPath, URL } from 'node:url'

import path from 'path';
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    build: {
        sourcemap: true,
        lib: {
            entry: path.resolve(__dirname, 'src/main.js'),
            name: 'codemirror',
            fileName: (format) => `codemirror.${format}.js`,
        },
        rollupOptions: {
            external: ['vue'],
            output: {
                globals: {
                    vue: 'Vue'
                },
            },
        },
    },
    optimizeDeps: {
        exclude: ['codemirror', '@codemirror/state', '@codemirror/language', '@codemirror/legacy-modes',
            '@codemirror/lang-vue', '@codemirror/autocomplete', '@lezer/highlight', '@codemirror/lint']
    },
    plugins: [
        vue(),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    define: {
        'process.env': {}
    }
})